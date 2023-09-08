package sejong.coffee.yun.domain.pay;

public interface Pay {
    public void payment(); // 결제 행위
    public void cancelPayment(PaymentCancelReason cancelReason); // 결제취소
}
