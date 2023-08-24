package ai.vital.anyscale.model

import ai.vital.anyscale.api.Chat
import ai.vital.anyscale.api.ChatRequest

abstract class AbstractChatModel extends AbstractModel {
	
	abstract public ChatRequest generatePredictionRequest(Chat currentChat) 
		
	
}
