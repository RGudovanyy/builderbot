package net.anviprojects.builderBot.telegram


import net.anviprojects.builderBot.model.BotUser
import net.anviprojects.builderBot.model.ConversationContext
import net.anviprojects.builderBot.model.MessageAdapter
import net.anviprojects.builderBot.services.CommonMessageService
import net.anviprojects.builderBot.services.MessageProcessor
import net.anviprojects.builderBot.services.SystemMessageService
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramFacade(val token : String, botOptions : DefaultBotOptions, val messageProcessor : MessageProcessor,
                     val commonMessageService: CommonMessageService, val systemMessageService: SystemMessageService) : TelegramLongPollingBot(botOptions) {


    val userContexts = HashMap<String, ConversationContext>()

    override fun getBotUsername() = "otr_builder_bot"

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {

        val botUser = BotUser(update.message.from.userName)
        lateinit var conversationContext : ConversationContext
        if (userContexts.containsKey(botUser.username)) {
            conversationContext = userContexts.get(botUser.username)!!
        } else {
            conversationContext = ConversationContext(botUser, messageProcessor, commonMessageService, systemMessageService)
            userContexts.put(botUser.username, conversationContext)
        }
        conversationContext.addMessageToConversation(MessageAdapter.adapt(update.message, TelegramChat(update.message.chatId.toString(), this)))
    }




}