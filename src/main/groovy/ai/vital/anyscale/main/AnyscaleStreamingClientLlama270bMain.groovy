package ai.vital.anyscale.main

import ai.vital.anyscale.api.Chat
import ai.vital.anyscale.api.ChatMessage
import ai.vital.anyscale.api.ChatMessageType
import ai.vital.anyscale.api.ChatRequest
import ai.vital.anyscale.api.ChatResponse
import ai.vital.anyscale.api.AnyscaleJavaClient
import ai.vital.anyscale.api.AnyscaleJavaStreamingClient
import ai.vital.anyscale.api.StreamResponseHandler
import ai.vital.anyscale.model.Llama2_70bChatModel

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.apache.log4j.BasicConfigurator


class AnyscaleStreamingClientLlama270bMain extends groovy.lang.Script {

	String apiKey = null
	
	static void main(args) {
		
		// sets up logging to defaults
		BasicConfigurator.configure()
		
		AnyscaleStreamingClientLlama270bMain app = new AnyscaleStreamingClientLlama270bMain()
		
		app.run()		
	}
	
	@Override
	public Object run() {
		
		// Get API Key
		
		String configFile = "./config/anyscaleClient.conf"
	
		Config conf = ConfigFactory.parseFileAnySyntax(new File(configFile))
	
		apiKey = conf.getString("apiKey")
		
		// Model to use
		Llama2_70bChatModel modelClass = new Llama2_70bChatModel()
		
		
		StreamResponseHandler handler = new StreamResponseHandler() {
	
			@Override
			void handleStreamResponse(Map dataMap) {
				
				
				println "HandleData: " + dataMap
				
				
			}
	
		}
		
		
			
		AnyscaleJavaStreamingClient modelClient = new AnyscaleJavaStreamingClient(apiKey, modelClass)
			
		Chat currentChat = new Chat()
		
		currentChat.systemPrompt = "You are named Haley, and you are a brilliant author of children's stories. In your stories, you follow Dan Harmon's story circle."
	
		ChatMessage cm1 = new ChatMessage()
		
		cm1.messageText = "Hi, my name is Haley! I'm here to assist you write children's stories."
			
		cm1.messageType = ChatMessageType.BOT
		
		currentChat.chatMessageList = [ cm1  ]
		
		String stateName = "New Jersey"
		
		String promptString = """
Write a short story set in ${stateName}.
The story should be about a monkey who saves the world by being very brave and smart.
The story has a twist ending.
Each part of the story circle should have a separate paragraph.
""".trim().replaceAll("\n","\\\\n")
				
		ChatMessage userChatMessage = new ChatMessage()
		
		userChatMessage.messageText = promptString 
		userChatMessage.messageType = ChatMessageType.USER
			
		// New Message:
		currentChat.userPrompt = userChatMessage.messageText
		
		ChatRequest request = modelClass.generatePredictionRequest(currentChat)
		
		ChatResponse response = modelClient.generatePrediction(request,  handler, 100_000)
		
		if(response == null || response.errorCode != 0) {
			
			println "Timeout or other error."
			
			System.exit(1)
		}
		
		String messageText = response.chatMessage.messageText
		
		String messageType = response.chatMessage.messageType
		
		println "${messageType}: ${messageText}"
	
		// Add into history:
		currentChat.chatMessageList.add(userChatMessage)
		
		currentChat.chatMessageList.add(response.chatMessage)
		
		
		System.exit(0)
		
			
	}
}
