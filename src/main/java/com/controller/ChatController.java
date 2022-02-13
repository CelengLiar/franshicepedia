package com.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.entity.Chat;
import com.model.respond.ChatRespond;
import com.model.respond.Response;
import com.repositories.ChatRepository;
import com.util.SaveImageUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@Controller
@Slf4j

public class ChatController {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private Gson gson = new Gson();

	@Autowired
	private ChatRepository chatRepository;

	public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
		super();
		this.simpMessagingTemplate = simpMessagingTemplate;
	}


	@PostMapping("post/image")
	public ResponseEntity<Object> uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile file,
			@RequestParam("userid") MultipartFile userId) throws IOException {
		String jsonString = new String(userId.getBytes());
		Chat chat = gson.fromJson(jsonString, Chat.class);
		log.info(chat.toString());
		SaveImageUtil.saveImage(chat.getSender(), chat.getMessage(), file);
		//simpan ke DB
		chatRepository.save(chat);
		//kirim triger ke receiver
		ChatRespond respond = ChatRespond.builder().message(chat.getMessage()).sender(chat.getSender())
				.messageType(chat.getMessageType()).build();
		Response<ChatRespond> response = Response.<ChatRespond>builder().status(HttpStatus.OK).respond(respond).build();
		simpMessagingTemplate.convertAndSendToUser(String.valueOf(chat.getReceiver()), "/msg", response);

		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@GetMapping("get/image")
	@ResponseBody
	public ResponseEntity<Object> getImage(@RequestParam String id, @RequestParam String name) {
		Resource resource = SaveImageUtil.getImage(id, name);
		if (resource.exists())
			log.info("GET IMAGE: " + resource.getFilename());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
	}

	@MessageMapping("/chat")
	public void chat(Chat chat) {
		log.info("Sender: " + chat.getSender() + " -> " + "Receiver: " + chat.getReceiver() + ", "
				+ chat.getMessage() + ", " + chat.getMessageType() + "]");

		ChatRespond respond = ChatRespond.builder().message(chat.getMessage()).sender(chat.getSender())
				.messageType(chat.getMessageType()).build();
		Response<ChatRespond> response = Response.<ChatRespond>builder().status(HttpStatus.OK).respond(respond).build();
		// simpan ke DB	
		chatRepository.save(chat);

		simpMessagingTemplate.convertAndSendToUser(String.valueOf(chat.getReceiver()), "/msg", response);
	}
}
