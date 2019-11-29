package acidsore;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.*;

public class NettyClientHandler extends SimpleChannelInboundHandler {
    private String storage="client_storage/";
    private String fileName;
    private int readLength = 1024;
    private int count = 1;

    private Runnable authAction;
    private Runnable finishAction;
    private Runnable deleteAction;
    private Runnable dataAction;
    private Runnable readyAction;
    public FileNames list;

    public void setAuthAction(Runnable authAction){ this.authAction = authAction;}
    public void setFinishAction(Runnable finishAction){ this.finishAction = finishAction;}
    public void setDeleteAction(Runnable deleteAction){ this.deleteAction = deleteAction;}
    public void setDataAction(Runnable dataAction){ this.dataAction = dataAction;}
    public void setReadyAction(Runnable readyAction){this.readyAction = readyAction;}

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
     //   System.out.println("answer from server");
        if (msg instanceof String) {
            String answ = (String) msg;
            System.out.println(answ);
            switch (answ) {
                case "AuthOk":
                    authAction.run();
                    break;
                case "finished":
                    finishAction.run();
                    break;
                case "deleted":
                    deleteAction.run();
                    break;
                case "data":
                    dataAction.run();
                    break;
                case "ready": {
                    fileName = ctx.channel().attr(NettyClient.fn).get();
                    if(fileName!=null){
                        File file = new File(storage + fileName);
                        int offset = 0;
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);// sending file content
                        while (bis.available() > 0) {
                            if (file.length() - offset > readLength) {
                                byte[] bb = new byte[readLength];
                                bis.read(bb, 0, readLength);
                                offset = offset + readLength;
                                ctx.writeAndFlush(bb);
                            } else {
                                byte[] bb = new byte[(int) (file.length() - offset)];
                                bis.read(bb, 0, (int) (file.length() - offset));
                                ctx.writeAndFlush(bb);
                                break;
                            }
                        }
                        bis.close();
                        fis.close();
                        readyAction.run();
                    }
                    break;
                }
            }
        }
        else if(msg instanceof byte[]){
            fileName = ctx.channel().attr(NettyClient.fn).get();
            if (!(new File(storage + fileName).exists())) {
               File file = new File(storage + fileName);
               msgDelivery(fileName, (byte[]) msg);
            }//check that we need to create new file
            else if ((new File(storage + fileName).exists())) {
          //      System.out.println(fileName+"!");
                String[] subs = fileName.split("\\.");
                fileName = subs[0] +count+"."+subs[1];
                msgDelivery(fileName, (byte[]) msg);
                count = count + 1;
            }
        }
        else if(msg instanceof FileNames){
            list = (FileNames) msg;
         //   System.out.println(list);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected!");
        ctx.fireChannelActive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    public void msgDelivery(String name, byte[] b) throws IOException {
        File file = new File(storage + fileName);
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(b);
        fos.close();
    }
}


