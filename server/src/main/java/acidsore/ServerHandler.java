package acidsore;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.*;
import static acidsore.AuthService.checkAuth;

public class ServerHandler extends SimpleChannelInboundHandler {

    private String storage;
    private String fileName = new String();
    private int readLength =1024;
    private int count = 1;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        String s = new String();
        Channel incoming = channelHandlerContext.channel();
        if(msg instanceof String) {
            s = (String) msg;
//            System.out.println(s);
            String[] toSplit = s.split(" ");
            if (toSplit[0].equals("auth")){
                if(checkAuth(toSplit[1],toSplit[2])) {
                    incoming.attr(NettyServer.folder).set(toSplit[1]);
                    storage ="server_storage/"+incoming.attr(NettyServer.folder).get()+"/";
                  //  System.out.println(storage);
                  incoming.writeAndFlush("AuthOk");
                }
            }
            if (toSplit[0].equals("content")) {
                File serverFolder = new File(storage);
                File[] listOfServerFile = serverFolder.listFiles();
                FileNames name = new FileNames();
                name.setContent(listOfServerFile);
                System.out.println(name.getContent());
                System.out.println(name.getContent().length);
                incoming.writeAndFlush(name);
                incoming.writeAndFlush("data");
            }
            else {
                fileName = toSplit[1];
                if(toSplit[0].equals("del"))
                {
                    File file = new File(storage+fileName);
                    if(file.exists()) file.delete();
                    incoming.writeAndFlush("deleted");
                }
                if (toSplit[0].equals("dwnl")) {
                    int offset = 0;
                    File file = new File(storage + fileName);
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    while (bis.available()>0) {  // sending file content
                        if (file.length() - offset > readLength) {
                            byte[] bb = new byte[readLength];
                            bis.read(bb, 0, readLength);
                            offset = offset + readLength;
                            incoming.writeAndFlush(bb);
                        } else {
                            byte[] bb = new byte[(int) (file.length() - offset)];
                            bis.read(bb, 0, (int) (file.length() - offset));
                            incoming.writeAndFlush(bb);
                            break;
                        }
                    }
                    bis.close();
                    fis.close();
                    incoming.writeAndFlush("finished");
                }
                if (toSplit[0].equals("send")) {
                    incoming.writeAndFlush("ready");
                }
            }
        }
        //creating and writing down the info
        else if(msg instanceof byte[]){
            if (!(new File(storage+fileName).exists())){
                File file = new File(storage + fileName);
                FileOutputStream fos = new FileOutputStream(file,true);
                System.out.println(msg);
                fos.write((byte[]) msg);
                fos.close();
            }//check that we need to create new file
            else if((new File(storage+fileName).exists())){
                String[] subs = fileName.split("\\.");
                fileName = subs[0] +count+"."+subs[1];
                File file = new File(storage + fileName);
                FileOutputStream fos = new FileOutputStream(file,true);
                System.out.println(msg);
                fos.write((byte[]) msg);
                fos.close();
                count = count+1;
            }
         incoming.writeAndFlush("got");
        }
    }
    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }
}
