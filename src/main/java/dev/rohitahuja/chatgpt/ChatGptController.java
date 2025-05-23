package dev.rohitahuja.chatgpt;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin
public class ChatGptController {

    private static final Logger _log = LoggerFactory.getLogger(ChatGptController.class);
    private final ChatClient chatClient;

    public ChatGptController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(
                                MessageWindowChatMemory.builder().
                                        chatMemoryRepository(new InMemoryChatMemoryRepository())
                                        .build())
                        .build())
                .build();
    }

    @GetMapping("")
    public String home() {
        return "index";
    }

    @HxRequest
    @PostMapping("/api/chat")
    public HtmxResponse generate(@RequestParam String message, Model model){
        _log.info("User Message: {}", message);

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        model.addAttribute("response", response);
        model.addAttribute("message", message);

        return HtmxResponse.builder()
                .view("response :: responseFragment")
                .view("recent-message-list :: messageFragment")
                .build();
    }



}
