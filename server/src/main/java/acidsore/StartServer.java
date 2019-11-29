package acidsore;

import java.io.IOException;

public class StartServer
{
    public static void main( String[] args )
    {
            new NettyServer(8081).run();
    }
}
