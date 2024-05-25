package com.uk.chatapp;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
public class ChatController {

    private final ChatRepository chatRepository;

    // 귓속말 할 때 사용
    // produces = MediaType.TEXT_EVENT_STREAM_VALUE: SSE 프로토콜
    @CrossOrigin
    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMessage(@PathVariable String sender, @PathVariable String receiver) {
        return chatRepository.mFindBySender(sender, receiver)
            .subscribeOn(Schedulers.boundedElastic());
    }


    @CrossOrigin
    @GetMapping(value = "/chat/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByUsername(@PathVariable Integer roomNum) {
        return chatRepository.mFindByRoomNum(roomNum)
            .subscribeOn(Schedulers.boundedElastic());
    }


    // Mono가 return값인 이유는 chat이 저장된 걸 보고 싶어서
    // return void여도 상관 X
    @CrossOrigin
    @PostMapping("/chat")
    public Mono<Chat> setMessage(@RequestBody Chat chat) {
        chat.setCreateAt(LocalDateTime.now());
        return chatRepository.save(chat);   // Object를 리턴하면 자동으로 JSON 변환 (MessageConverter)
    }
}
