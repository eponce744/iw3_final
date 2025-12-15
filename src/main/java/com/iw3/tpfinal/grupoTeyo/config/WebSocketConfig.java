package com.iw3.tpfinal.grupoTeyo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		//Endpoint para conectarse al WebSocket ()
		registry.addEndpoint("/ws")
				.setAllowedOrigins("http://localhost:5500")  // <--- Aqui habilitamos el origen de las solicitudes hacia el Backend 5173
				.withSockJS();	
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		
		// topic: Prefijo para mensajes que el Backend envía a los clientes suscriptos
		registry.enableSimpleBroker("/topic");
		
		// /app: Prefijo para mensajes que el Cliente envia al backend
		registry.setApplicationDestinationPrefixes("/app");
	}
	

}
