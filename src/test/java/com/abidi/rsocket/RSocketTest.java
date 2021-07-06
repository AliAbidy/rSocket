package com.abidi.rsocket;

import com.abidi.rsocket.dto.RequestDto;
import com.abidi.rsocket.dto.ResponseDto;
import com.abidi.rsocket.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RSocketTest {
    private RSocket rSocket;

    @BeforeAll
    public void setup() {
        this.rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
    }

    @RepeatedTest(10)
    public void fireAndForget() {
        Payload payload = ObjectUtil.toPayLoad(new RequestDto(5));
        Mono<Void> mono = this.rSocket.fireAndForget(payload);

        StepVerifier.create(mono)
                .verifyComplete();

    }

    @Test
    public void requestResponse() {
        Payload payload = ObjectUtil.toPayLoad(new RequestDto(5));
        Mono<ResponseDto> mono = this.rSocket.requestResponse(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();

    }
    @Test
    public void requestStream() {
        Payload payload = ObjectUtil.toPayLoad(new RequestDto(5));
        Flux<ResponseDto> flux = this.rSocket.requestStream(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();

    }
}
