package sejong.coffee.yun.infra.fake;

import sejong.coffee.yun.infra.TossAPIService;
import sejong.coffee.yun.mapper.CustomMapper;

import java.io.IOException;

import static sejong.coffee.yun.dto.CardPaymentDto.Request;
import static sejong.coffee.yun.dto.CardPaymentDto.Response;

public class FakeAPIService implements TossAPIService {

    public Request request;
    public Response response;
    public CustomMapper customMapper;

    @Override
    public Response callExternalAPI(Request cardPaymentDto) throws IOException, InterruptedException {
        this.request = cardPaymentDto;
        return customMapper.map(request, Response.class);

    }
}
