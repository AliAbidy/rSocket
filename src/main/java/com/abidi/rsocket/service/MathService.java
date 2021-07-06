package com.abidi.rsocket.service;

import com.abidi.rsocket.dto.RequestDto;
import com.abidi.rsocket.dto.ResponseDto;
import com.abidi.rsocket.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.management.timer.Timer;
import java.time.Duration;

public class MathService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Received: "+ ObjectUtil.toObject(payload, RequestDto.class));

        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {

        return Mono.fromSupplier(() -> {
            RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);

            ResponseDto responseDto = new ResponseDto(requestDto.getInput(), requestDto.getInput() * requestDto.getInput());

            return ObjectUtil.toPayLoad(responseDto);

        });

    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);

        return Flux.range(1, 10)
                .map(i -> i * requestDto.getInput())
                .map(i -> new ResponseDto(requestDto.getInput(), i))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .map(ObjectUtil::toPayLoad);


    }
}
