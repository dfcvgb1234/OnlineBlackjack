package com.company.shared;

import com.company.shared.messages.Message;

public interface Component {
    Class<? extends Message> getMessageType();
}
