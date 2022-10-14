package cn.sakuraex.sakuraexplug.util;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.message.data.QuoteReply;

public class MessageUtil {
	public static MessageChainBuilder groupQuoteAndAtMCB(OnlineMessageSource source, Member sender) {
		return new MessageChainBuilder().append(new QuoteReply(source)).append(new At(sender.getId()));
	}
}
