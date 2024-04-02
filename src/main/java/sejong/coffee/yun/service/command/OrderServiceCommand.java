package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.dto.cart.CartDto;
import sejong.coffee.yun.redis.tx.RedisTransaction;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.cartitem.CartItemRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceCommand {

    // interface : green, class : yellow
    private final OrderRepository orderRepository;
    private final Calculator calculator;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final RedisOperations<String, String> redisOperations;

    public Order order(Long memberId, LocalDateTime now) {

        Cart cart = cartRepository.findByMember(memberId);

        List<CartDto.StockRecord> stockRecords = cartItemRepository.stockRecord(cart.getId());

        increaseMenuStockAndOrderCount(memberId, stockRecords);

        isCheckOverMenuStock(memberId, stockRecords);

        //addMenuOrderCountAndSubQuantity(memberId);

        Money money = calculator.calculateMenus(cart.getMember(), cart.convertToMenus());

        Order order = Order.createOrder(cart, money, now);

        return orderRepository.save(order);
    }

    /**
     * 유저가 주문한 상품 재고 증가 및 유저의 주문 개수 증가
     * @param memberId
     * @param stockRecords
     */
    private void increaseMenuStockAndOrderCount(Long memberId, List<CartDto.StockRecord> stockRecords) {
        stockRecords.forEach(stockRecord -> {
            // redis tx
            RedisTransaction.transaction(redisOperations, operations -> {
                this.increaseStock(stockRecord);
                this.increaseOrderCount(stockRecord, memberId);
            });
        });
    }

    /**
     * 올바르게 증가 했는지 검사
     * @param stockRecords
     */
    private void isCheckOverMenuStock(Long memberId, List<CartDto.StockRecord> stockRecords) {
        stockRecords.forEach(stockRecord -> {
            int totalQuantity = menuRepository.findById(stockRecord.menuId()).getQuantity();
            int totalUserUsedStock = menuRepository.usedTotalUserStockCount(redisOperations, stockRecord.menuId()).intValue();
            if(totalQuantity < totalUserUsedStock) {

                RedisTransaction.transaction(redisOperations, operations -> {
                    this.decreaseStock(stockRecord);
                    this.decreaseOrderCount(stockRecord, memberId);
                });

                throw ExceptionControl.MENU_NOT_ENOUGH_QUANTITY.throwException();
            }
        });
    }

    /**
     * redis transaction 적용한 현재 유저가 고른 재고의 증가
     * @param stockRecord
     */
    private void increaseStock(CartDto.StockRecord stockRecord) {
        menuRepository.increaseStock(redisOperations, stockRecord.menuId(), stockRecord.menuCount().intValue());
    }

    private void decreaseStock(CartDto.StockRecord stockRecord) {
        menuRepository.decreaseStock(redisOperations, stockRecord.menuId(), stockRecord.menuCount().intValue());
    }

    /**
     * 유저의 주문한 개수 증가
     *
     * @param stockRecord
     * @param memberId
     */
    private void increaseOrderCount(CartDto.StockRecord stockRecord, Long memberId) {
        userRepository.increaseOrderCount(redisOperations, memberId, stockRecord.menuCount().intValue());
    }

    private void decreaseOrderCount(CartDto.StockRecord stockRecord, Long memberId) {
        userRepository.decreaseOrderCount(redisOperations, memberId, stockRecord.menuCount().intValue());
    }

    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId);

        order.cancel();
    }

    @Deprecated
    private Menu addMenuOrderCountAndSubQuantity(Long menuId) {
        Menu findMenu = menuRepository.findById(menuId);

        findMenu.isCheckMenuQuantity();

        findMenu.subQuantity();
        findMenu.addOrderCount();

        return findMenu;
    }
}
