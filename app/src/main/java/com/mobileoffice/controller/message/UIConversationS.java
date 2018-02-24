//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mobileoffice.controller.message;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.color;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.MentionedInfo.MentionedType;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.ReceivedStatus;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.VoiceMessage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UIConversationS implements Parcelable {
    private static final String TAG = "UIConversationS";
    private String conversationTitle;
    private Uri portrait;
    private Spannable conversationContent;
    private MessageContent messageContent;
    private long conversationTime;
    private int unReadMessageCount;
    private boolean isTop;
    private ConversationType conversationType;
    private SentStatus sentStatus;
    private ReceivedStatus receivedStatus;
    private String targetId;
    private String senderId;
    private boolean isGathered;
    private Map<String, Integer> gatheredConversations;
    private boolean notificationBlockStatus;
    private String draft;
    private int latestMessageId;
    private boolean extraFlag;
    private boolean isMentioned;
    public UserInfo senderUserInfo;
    private Set<String> nicknameIds;
    private UIConversationS.UnreadRemindType mUnreadType;
    public static final Creator<UIConversationS> CREATOR = new Creator() {
        public UIConversationS createFromParcel(Parcel source) {
            return new UIConversationS();
        }

        public UIConversationS[] newArray(int size) {
            return new UIConversationS[size];
        }
    };

    public static UIConversationS obtain(Message message, boolean isGathered) {
        UIConversationS uiConversation = new UIConversationS();
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        Uri portrait = null;
        String title;
        if(isGathered) {
            title = RongContext.getInstance().getGatheredConversationTitle(conversationType);
        } else {
            title = RongContext.getInstance().getConversationTemplate(conversationType.getName()).getTitle(targetId);
            portrait = RongContext.getInstance().getConversationTemplate(conversationType.getName()).getPortraitUri(targetId);
        }

        ConversationKey key = ConversationKey.obtain(targetId, conversationType);
        ConversationNotificationStatus notificationStatus = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
        uiConversation.updateConversation(message, isGathered);
        uiConversation.conversationTitle = title;
        uiConversation.notificationBlockStatus = notificationStatus == null || notificationStatus.equals(ConversationNotificationStatus.NOTIFY);
        uiConversation.portrait = portrait;
        if(conversationType.equals(ConversationType.GROUP)) {
            GroupUserInfo groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(message.getTargetId(), message.getSenderUserId());
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if(groupUserInfo != null && userInfo != null) {
                uiConversation.senderUserInfo = new UserInfo(message.getSenderUserId(), groupUserInfo.getNickname(), userInfo.getPortraitUri());
                uiConversation.nicknameIds.add(message.getSenderUserId());
            } else {
                uiConversation.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            }
        } else {
            uiConversation.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
        }

        uiConversation.senderId = message.getSenderUserId();
        uiConversation.gatheredConversations.put(message.getConversationType().getName() + message.getTargetId(), Integer.valueOf(0));
        return uiConversation;
    }

    public void updateConversation(Message message, boolean isGathered) {
        MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
        MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
        boolean mentioned = mentionedInfo != null && (mentionedInfo.getType().equals(MentionedType.ALL) || mentionedInfo.getType().equals(MentionedType.PART) && mentionedInfo.getMentionedUserIdList() != null && mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()));
        this.conversationType = message.getConversationType();
        this.targetId = message.getTargetId();
        this.receivedStatus = message.getReceivedStatus();
        this.sentStatus = message.getSentStatus();
        this.conversationTime = message.getSentTime();
        this.isGathered = isGathered;
        this.messageContent = message.getContent();
        this.latestMessageId = message.getMessageId();
        this.isMentioned = !isGathered && (this.isMentioned || mentioned);
        boolean isCount = msgTag != null && (msgTag.flag() & 3) == 3 && message.getMessageDirection().equals(MessageDirection.RECEIVE);
        this.unReadMessageCount = isCount?this.unReadMessageCount + 1:this.unReadMessageCount;
        if(isGathered && isCount) {
            String groupUserInfo = this.conversationType.getName() + this.targetId;
            Set userInfo = this.gatheredConversations.keySet();
            if(userInfo.contains(groupUserInfo)) {
                int count = ((Integer)this.gatheredConversations.get(groupUserInfo)).intValue();
                this.gatheredConversations.put(groupUserInfo, Integer.valueOf(count + 1));
            } else {
                this.gatheredConversations.put(groupUserInfo, Integer.valueOf(1));
            }
        }

        if(!message.getSenderUserId().equals(this.senderId)) {
            if(this.conversationType.equals(ConversationType.GROUP)) {
                GroupUserInfo groupUserInfo1 = RongUserInfoManager.getInstance().getGroupUserInfo(message.getTargetId(), message.getSenderUserId());
                UserInfo userInfo1 = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
                if(groupUserInfo1 != null && userInfo1 != null) {
                    this.senderUserInfo = new UserInfo(message.getSenderUserId(), groupUserInfo1.getNickname(), userInfo1.getPortraitUri());
                    this.nicknameIds.add(message.getSenderUserId());
                } else {
                    this.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
                }
            } else {
                this.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            }

            this.senderId = message.getSenderUserId();
        }

        this.buildContent(this.senderUserInfo);
    }

    public static UIConversationS obtain(Conversation conversation, boolean isGathered) {
        UIConversationS uiConversation = new UIConversationS();
        uiConversation.gatheredConversations.put(conversation.getConversationType().getName() + conversation.getTargetId(), Integer.valueOf(0));
        uiConversation.updateConversation(conversation, isGathered);
        return uiConversation;
    }

    public void updateConversation(Conversation conversation, boolean isGathered) {
        String portrait = null;
        if(conversation.getSentTime() >= this.conversationTime) {
            String title;
            if(isGathered) {
                title = RongContext.getInstance().getGatheredConversationTitle(conversation.getConversationType());
            } else {
                title = this.conversationTitle == null?conversation.getConversationTitle():this.conversationTitle;
                if(TextUtils.isEmpty(title)) {
                    title = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getTitle(conversation.getTargetId());
                }

                portrait = this.portrait != null?this.portrait.toString():conversation.getPortraitUrl();
                if(TextUtils.isEmpty(portrait)) {
                    Uri groupUserInfo = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getPortraitUri(conversation.getTargetId());
                    portrait = groupUserInfo != null?groupUserInfo.toString():null;
                }
            }

            this.conversationType = conversation.getConversationType();
            this.targetId = conversation.getTargetId();
            this.conversationTitle = title;
            this.portrait = portrait != null?Uri.parse(portrait):null;
            this.receivedStatus = conversation.getReceivedStatus();
            this.sentStatus = conversation.getSentStatus();
            this.conversationTime = conversation.getSentTime();
            this.notificationBlockStatus = conversation.getNotificationStatus().equals(ConversationNotificationStatus.NOTIFY);
            this.draft = isGathered?null:conversation.getDraft();
            this.isGathered = isGathered;
            this.isTop = !isGathered && conversation.isTop();
            this.messageContent = conversation.getLatestMessage();
            this.latestMessageId = conversation.getLatestMessageId();
            this.senderId = conversation.getSenderUserId();
            this.isMentioned = !isGathered && conversation.getMentionedCount() > 0;
            if(this.conversationType.equals(ConversationType.GROUP)) {
                GroupUserInfo groupUserInfo1 = RongUserInfoManager.getInstance().getGroupUserInfo(this.targetId, this.senderId);
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(this.senderId);
                if(groupUserInfo1 != null && userInfo != null) {
                    this.senderUserInfo = new UserInfo(this.senderId, groupUserInfo1.getNickname(), userInfo.getPortraitUri());
                    this.nicknameIds.add(this.senderId);
                } else {
                    this.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(this.senderId);
                }
            } else {
                this.senderUserInfo = RongUserInfoManager.getInstance().getUserInfo(this.senderId);
            }

            this.buildContent(this.senderUserInfo);
        }

        if(isGathered) {
            this.unReadMessageCount += conversation.getUnreadMessageCount();
            this.gatheredConversations.put(conversation.getConversationType().getName() + conversation.getTargetId(), Integer.valueOf(conversation.getUnreadMessageCount()));
        } else {
            this.unReadMessageCount = conversation.getUnreadMessageCount();
        }

    }

    private void buildContent(UserInfo userInfo) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(this.messageContent == null) {
            builder.append("");
        } else {
            ProviderTag providerTag = RongContext.getInstance().getMessageProviderTag(this.messageContent.getClass());
            MessageProvider messageProvider = RongContext.getInstance().getMessageTemplate(this.messageContent.getClass());
            if(providerTag != null && messageProvider != null) {
                Spannable summary = messageProvider.getContentSummary(this.messageContent);
                boolean isShowName = providerTag.showSummaryWithName();
                String curUserId = RongIM.getInstance().getCurrentUserId();
                if(summary == null) {
                    builder.append("");
                } else {
                    if(this.messageContent instanceof VoiceMessage) {
                        boolean name = this.receivedStatus.isListened();
                        if(!this.senderId.equals(curUserId) && !name) {
                            summary.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(color.rc_voice_color)), 0, summary.length(), 33);
                        } else {
                            summary.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(color.rc_text_color_secondary)), 0, summary.length(), 33);
                        }
                    }

                    String name1;
                    if(this.isGathered) {
                        name1 = RongContext.getInstance().getConversationTemplate(this.conversationType.getName()).getTitle(this.targetId);
                        builder.append(String.format("%s: ", new Object[]{name1})).append(summary);
                    } else if(!TextUtils.isEmpty(this.draft) && !this.isMentioned) {
                        builder.append(this.draft);
                    } else if(this.senderId.equals(curUserId)) {
                        builder.append(summary);
                    } else {
                        name1 = userInfo != null?userInfo.getName():this.senderId;
                        if((this.conversationType.equals(ConversationType.GROUP) || this.conversationType.equals(ConversationType.DISCUSSION)) && isShowName) {
                            builder.append(String.format("%s: ", new Object[]{name1})).append(summary);
                        } else {
                            builder.append(summary);
                        }
                    }
                }
            } else {
                builder.append("");
            }
        }

        this.conversationContent = builder;
    }

    public void clearLastMessage() {
        this.messageContent = null;
        this.latestMessageId = 0;
        this.conversationContent = null;
        this.unReadMessageCount = 0;
        this.isMentioned = false;
        this.sentStatus = SentStatus.DESTROYED;
    }

    public void clearUnRead(ConversationType conversationType, String targetId) {
        if(this.isGathered) {
            String key = conversationType.getName() + targetId;
            this.gatheredConversations.put(key, Integer.valueOf(0));
            this.unReadMessageCount = 0;
            Collection collection = this.gatheredConversations.values();

            int count;
            for(Iterator i$ = collection.iterator(); i$.hasNext(); this.unReadMessageCount += count) {
                count = ((Integer)i$.next()).intValue();
            }
        } else {
            this.unReadMessageCount = 0;
            this.isMentioned = false;
        }

    }

    public void updateConversation(UserInfo userInfo) {
        if(this.isGathered) {
            this.conversationTitle = RongContext.getInstance().getGatheredConversationTitle(this.conversationType);
            this.buildContent(userInfo);
        } else if(!this.conversationType.equals(ConversationType.GROUP) && !this.conversationType.equals(ConversationType.DISCUSSION)) {
            if(userInfo.getUserId().equals(this.targetId)) {
                this.conversationTitle = userInfo.getName();
                this.portrait = userInfo.getPortraitUri();
                this.buildContent(userInfo);
            }
        } else if(this.senderId != null && userInfo.getUserId().equals(this.senderId)) {
            this.senderUserInfo = userInfo;
            this.buildContent(userInfo);
        }

    }

    public void updateConversation(Group group) {
        if(this.isGathered) {
            this.conversationTitle = RongContext.getInstance().getGatheredConversationTitle(this.conversationType);
            this.buildContent(this.senderUserInfo);
        } else if(this.conversationType.equals(ConversationType.GROUP) && group.getId().equals(this.targetId)) {
            this.conversationTitle = group.getName();
            this.portrait = group.getPortraitUri();
        }

    }

    public void updateConversation(Discussion discussion) {
        if(this.isGathered) {
            this.conversationTitle = RongContext.getInstance().getGatheredConversationTitle(this.conversationType);
            this.buildContent(this.senderUserInfo);
        } else if(this.conversationType.equals(ConversationType.DISCUSSION) && discussion.getId().equals(this.targetId)) {
            this.conversationTitle = discussion.getName();
        }

    }

    public void updateConversation(GroupUserInfo groupUserInfo) {
        UserInfo userInfo = new UserInfo(groupUserInfo.getUserId(), groupUserInfo.getNickname(), this.portrait);
        this.addNickname(groupUserInfo.getUserId());
        this.senderUserInfo = new UserInfo(groupUserInfo.getUserId(), groupUserInfo.getNickname(), (Uri)null);
        this.buildContent(userInfo);
    }

    public boolean getExtraFlag() {
        return this.extraFlag;
    }

    public void setExtraFlag(boolean extraFlag) {
        this.extraFlag = extraFlag;
    }

    public UIConversationS() {
        this.mUnreadType = UIConversationS.UnreadRemindType.REMIND_WITH_COUNTING;
        this.nicknameIds = new HashSet();
        this.gatheredConversations = new HashMap();
    }

    public void setUIConversationTitle(String title) {
        this.conversationTitle = title;
    }

    public String getUIConversationTitle() {
        return this.conversationTitle;
    }

    public void setIconUrl(Uri iconUrl) {
        this.portrait = iconUrl;
    }

    public Uri getIconUrl() {
        return this.portrait;
    }

    public void setConversationContent(Spannable content) {
        this.conversationContent = content;
    }

    public Spannable getConversationContent() {
        return this.conversationContent;
    }

    public void setMessageContent(MessageContent content) {
        this.messageContent = content;
    }

    public MessageContent getMessageContent() {
        return this.messageContent;
    }

    public void setUIConversationTime(long time) {
        this.conversationTime = time;
    }

    public long getUIConversationTime() {
        return this.conversationTime;
    }

    public void setUnReadMessageCount(int count) {
        this.unReadMessageCount = count;
    }

    public int getUnReadMessageCount() {
        return this.unReadMessageCount;
    }

    public void setTop(boolean value) {
        this.isTop = value;
    }

    public boolean isTop() {
        return this.isTop;
    }

    public void setConversationType(ConversationType type) {
        this.conversationType = type;
    }

    public ConversationType getConversationType() {
        return this.conversationType;
    }

    public void setSentStatus(SentStatus status) {
        this.sentStatus = status;
    }

    public SentStatus getSentStatus() {
        return this.sentStatus;
    }

    public void setConversationTargetId(String id) {
        this.targetId = id;
    }

    public String getConversationTargetId() {
        return this.targetId;
    }

    public void setConversationSenderId(String id) {
        this.senderId = id;
    }

    public String getConversationSenderId() {
        return this.senderId;
    }

    public void setConversationGatherState(boolean state) {
        this.isGathered = state;
    }

    public boolean getConversationGatherState() {
        return this.isGathered;
    }

    public void setNotificationBlockStatus(boolean status) {
        this.notificationBlockStatus = status;
    }

    public boolean getNotificationBlockStatus() {
        return this.notificationBlockStatus;
    }

    public void setDraft(String content) {
        this.draft = content;
    }

    public String getDraft() {
        return this.draft;
    }

    public void setLatestMessageId(int id) {
        this.latestMessageId = id;
    }

    public int getLatestMessageId() {
        return this.latestMessageId;
    }

    public void addNickname(String userId) {
        this.nicknameIds.add(userId);
    }

    public void removeNickName(String userId) {
        this.nicknameIds.remove(userId);
    }

    public boolean hasNickname(String userId) {
        return this.nicknameIds.contains(userId);
    }

    public void setMentionedFlag(boolean flag) {
        this.isMentioned = flag;
    }

    public boolean getMentionedFlag() {
        return this.isMentioned;
    }

    public void setUnreadType(UIConversationS.UnreadRemindType type) {
        this.mUnreadType = type;
    }

    public UIConversationS.UnreadRemindType getUnReadType() {
        return this.mUnreadType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    public static enum UnreadRemindType {
        NO_REMIND,
        REMIND_ONLY,
        REMIND_WITH_COUNTING;

        private UnreadRemindType() {
        }
    }
}
