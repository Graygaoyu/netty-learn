package com.gencent.sender;

import com.gencent.client.ClientSession;
import com.gencent.concurrent.CallbackTask;
import com.gencent.concurrent.CallbackTaskScheduler;
import com.gencent.pojo.MessageProto;
import com.gencent.pojo.User;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public abstract class BaseSender {

    private User user;

    private ClientSession session;

    public boolean isConnected()
    {
        if (null == session)
        {
            return false;
        }

        return session.isConnected();
    }

    public boolean isLogin()
    {
        if (null == session)
        {
            return false;
        }

        return session.isLogin();
    }

    public void sendMsg(MessageProto.Message message)
    {
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (null == getSession())
                {
                    throw new Exception("session is null");
                }

                if (!isConnected())
                {
                    System.out.println("连接还没成功");
                    throw new Exception("连接还没成功");
                }
                final Boolean[] isSuccess = {false};

                ChannelFuture f = getSession().witeAndFlush(message);
                f.addListener(future -> {
                    if (future.isSuccess()) {
                        isSuccess[0] = true;
                    }
                });

                try
                {
                    f.sync();
                } catch (InterruptedException e)
                {
                    isSuccess[0] = false;
                    e.printStackTrace();
                    throw new Exception("error occur");
                }

                return isSuccess[0];
            }

            @Override
            public void onBack(Boolean b) {
                if (b)
                {
                    BaseSender.this.sendSucced(message);

                } else
                {
                    BaseSender.this.sendfailed(message);

                }
            }

            @Override
            public void onException(Throwable t) {
                BaseSender.this.sendException(message);
                t.printStackTrace();
            }
        });
    }

    protected void sendSucced(MessageProto.Message message)
    {
        System.out.println("发送成功");

    }

    protected void sendfailed(MessageProto.Message message)
    {
        System.out.println("发送失败");
    }

    protected void sendException(MessageProto.Message message)
    {
        System.out.println("发送消息出现异常");

    }
}
