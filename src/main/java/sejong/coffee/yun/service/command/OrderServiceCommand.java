package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.CartItem;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.service.OrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceCommand implements OrderService {

    private final OrderRepository orderRepository;
    private final Calculator calculator;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY = "rank";

    @Override
    public Order order(Long memberId, LocalDateTime now) {

        Cart cart = cartRepository.findByMember(memberId);

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        cart.getCartItems().stream().map(CartItem::getMenu).map(Menu::getId).map(this::addMenuOrderCountAndSubQuantity)
                .forEach(menu -> zSetOperations.incrementScore(REDIS_KEY, menu.getTitle(), 1));

        Money money = calculator.calculateMenus(cart.getMember(), cart.convertToMenus());

        Order order = Order.createOrder(cart, money, now);

        //cart.clearCartItems();

        return orderRepository.save(order);
    }

    @Override
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId);

        order.cancel();
    }


    private Menu addMenuOrderCountAndSubQuantity(Long menuId) {
        Menu findMenu = menuRepository.findById(menuId);

        findMenu.subQuantity();
        findMenu.addOrderCount();

        return findMenu;
    }
}
