<template>
  <div class="invite-container">
    <div
      v-for="(item, index) in invitationFeatureDetails"
      :key="index"
      class="invite-item"
      @click="item.function()"
    >
      <svg-icon class="icon" :icon="item.icon" />
      <div class="invite-title">{{ t(item.text) }}</div>
    </div>
    <Contacts
      :visible="showContacts"
      :contacts="contacts"
      :selected-list="inviteeList"
      :disabled-list="remoteEnteredUserList"
      @input="showContacts = $event"
      @confirm="contactsConfirm"
    />
    <Dialog
      v-model="showRoomInvite"
      :title="
        t('sb invites you to join the conference', { name: userName || userId })
      "
      :modal="true"
      :show-close="true"
      :close-on-click-modal="true"
      width="540px"
      :append-to-body="true"
      :title-icon="SuccessIcon"
    >
      <div class="invite-member">
        <div
          v-for="(item, index) in conferenceInviteList"
          :key="index"
          class="invite-member-container"
        >
          <span class="invite-member-title">{{ t(item.title) }}</span>
          <span class="invite-member-content" :title="item.content">
            {{ item.content }}
          </span>
          <svg-icon
            v-if="item.isShowCopyIcon"
            class="copy"
            :icon="copyIcon"
            @click="onCopy(item.content)"
          />
        </div>
      </div>
      <template #footer>
        <span>
          <tui-button
            class="dialog-button"
            size="default"
            @click="copyRoomIdAndRoomLink()"
          >{{ t('Copy the conference number and link') }}
          </tui-button>
        </span>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import useRoomInviteControl from './useRoomInviteHooks';
import SvgIcon from '../common/base/SvgIcon.vue';
import TuiButton from '../common/base/Button.vue';
import Contacts from '../ScheduleConference/Contacts.vue';
import SuccessIcon from '../common/icons/SuccessIcon.vue';
import copyIcon from '../common/icons/CopyIcon.vue';
import Dialog from '../common/base/Dialog';
const {
  t,
  invitationFeatureDetails,
  showContacts,
  contactsConfirm,
  contacts,
  remoteEnteredUserList,
  showRoomInvite,
  userId,
  userName,
  conferenceInviteList,
  onCopy,
  copyRoomIdAndRoomLink,
  inviteeList,
} = useRoomInviteControl();
</script>

<style lang="scss" scoped>
.invite-container {
  position: absolute;
  bottom: 72px;
  padding: 10px 30px 0 20px;
  background-color: var(--background-color-2);
  border-radius: 15px;
  box-shadow: 0 -8px 30px var(--footer-shadow-color);

  .invite-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    cursor: pointer;

    &:hover {
      cursor: pointer;
      background: var(--hover-bg-color);
    }

    .icon {
      width: 16px;
      height: 16px;
    }

    .invite-title {
      padding-left: 5px;
      font-family: 'PingFang SC';
      font-size: 12px;
      font-weight: 400;
      line-height: 20px;
      text-align: left;
    }
  }
}

.invite-member {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 10px;

  .invite-member-container {
    display: flex;
    align-items: stretch;
    min-width: 400px;
    font-size: 14px;
    font-weight: 400;
    line-height: 20px;
    color: #4f586b;

    .invite-member-title {
      flex-basis: 18%;
    }

    .invite-member-content {
      max-width: 360px;
      overflow: hidden;
      font-weight: 500;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .copy {
      width: 20px;
      height: 20px;
      margin-left: 8px;
      color: var(--active-color-1);
      cursor: pointer;
    }
  }
}

.tui-theme-black .invite-container {
  --hover-bg-color: rgba(79, 88, 107, 0.2);
}

.tui-theme-white .invite-container {
  --hover-bg-color: rgba(213, 224, 242, 0.3);
}
</style>
