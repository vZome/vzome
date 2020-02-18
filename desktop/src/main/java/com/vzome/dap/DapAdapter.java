package com.vzome.dap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ConfigurationDoneArguments;
import org.eclipse.lsp4j.debug.ContinueArguments;
import org.eclipse.lsp4j.debug.ContinueResponse;
import org.eclipse.lsp4j.debug.DisconnectArguments;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.NextArguments;
import org.eclipse.lsp4j.debug.PauseArguments;
import org.eclipse.lsp4j.debug.Scope;
import org.eclipse.lsp4j.debug.ScopesArguments;
import org.eclipse.lsp4j.debug.ScopesResponse;
import org.eclipse.lsp4j.debug.SetBreakpointsArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsResponse;
import org.eclipse.lsp4j.debug.SetExceptionBreakpointsArguments;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StackTraceArguments;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.StepInArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.ThreadsResponse;
import org.eclipse.lsp4j.debug.launch.DSPLauncher;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.vorthmann.ui.Controller;

public class DapAdapter implements IDebugProtocolServer
{
    private static final Logger logger = Logger .getLogger( "com.vzome.dap.DapAdapter" );
    
    private static final int BACKLOG = 100;
    private static final long THREAD_ID = 0;

    private OutputStream out;
    private InputStream in;
    private ServerSocket server;
    private Thread thread;
    private IDebugProtocolClient client;

    private Controller appController;

    private Controller docController;
    
    private Source source = new Source();
    
    public void startServer( int port, Controller controller )
    {
        this.appController = controller;
        this .thread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    server = new ServerSocket( port, BACKLOG );
                    while (true) {
                        try {
                            showMsg( "\nWaiting for debug connection..." );
                            Socket socket = server .accept();
                            showMsg( "Connection accepted from " + socket .getInetAddress() .getHostName() );

                            out = socket .getOutputStream();
                            out .flush();
                            in = socket .getInputStream();
                            showMsg( "\nI/O Stream is ready." );

                            Launcher<IDebugProtocolClient> launcher = DSPLauncher .createServerLauncher( DapAdapter.this, in, out );
                            
                            client = launcher .getRemoteProxy();

                            Future<Void> listening = launcher .startListening();
                        } catch (EOFException ex) {
                            showMsg("\nServer connection closed");
                            try {
                                out .close();
                                in .close();
//                                socket .close();  // we cannot close now, since we are listening async
                            } catch (IOException ex2) {
                            }
                        }
                    }
                } catch (IOException e) {
                }
            }
            
        };
        this .thread .start();
    }

    private void showMsg( final String msg )
    {
        System.out.println( msg );
    }

    @Override
    public CompletableFuture<Capabilities> initialize( InitializeRequestArguments args )
    {
        if ( logger .isLoggable( Level.INFO ) )
            logger .info( "initialize()" );

        Capabilities capabilities = new Capabilities();
        capabilities .setSupportsConfigurationDoneRequest( false );
        capabilities .setSupportsExceptionOptions( false );
        capabilities .setSupportsFunctionBreakpoints( false );
        return CompletableFuture .completedFuture( capabilities );
    }

    @Override
    public CompletableFuture<Void> launch( Map<String, Object> args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "launch() args: " + args .toString() );
            
            String path = (String) args .get( "modelFile" );
            File file = new File( path );
            this .source .setPath( path );
            this .source .setName( file .getName() );
            this .source .setAdapterData( "vzome-adapter-data" );

            boolean stopOnEntry = (Boolean) args .get( "stopOnEntry" );
            if ( stopOnEntry ) {
                this .appController .doFileAction( "openDeferringRedo", file );
                
                this .client .initialized();
                
                // If we don't send a stopped event, the debugger will assume
                //  the "program" is running, and won't offer the step controls.
                StoppedEventArguments arguments = new StoppedEventArguments();
                arguments .setReason( "entry" );
                arguments .setThreadId( THREAD_ID );
                this .client .stopped( arguments );
            } else {
                this .appController .doFileAction( "open", file );
            }
            this .docController = this .appController .getSubController( path ) .getSubController( "undoRedo" );
        } );
    }

    @Override
    public CompletableFuture<Void> disconnect( DisconnectArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "disconnect() args: " + args .toString() );
            
//            this .docController .actionPerformed( this, "close" ); // TODO: implement the right mechanism
        } );
    }

    @Override
    public CompletableFuture<ThreadsResponse> threads()
    {
        if ( logger .isLoggable( Level.INFO ) )
            logger .info( "threads()" );

        // We have to implement this request, since it will be made whenever
        //  the debugger gets a stopped event.  Ditto stackTrace.
        org.eclipse.lsp4j.debug.Thread thread = new org.eclipse.lsp4j.debug.Thread();
        thread .setId( THREAD_ID );
        thread .setName( "SingleThread" );
        
        ThreadsResponse response = new ThreadsResponse();
        response .setThreads( new org.eclipse.lsp4j.debug.Thread[] { thread } );
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<StackTraceResponse> stackTrace( StackTraceArguments args )
    {
        if ( logger .isLoggable( Level.INFO ) )
            logger .info( "stackTrace() args: " + args .toString() );

        // We have to implement this request, since it will be made whenever
        //  the debugger gets a stopped event.  Ditto threads.
        StackFrame frame = new StackFrame();
        String lineNum = this .docController .getProperty( "line.number" );
        frame .setLine( Long .parseLong( lineNum ) );
        frame .setId( THREAD_ID );
        frame .setName( "EditHistory" );
        frame .setSource( this .source );
        frame .setColumn( 0l );
        
        StackTraceResponse response = new StackTraceResponse();
        response .setStackFrames( new StackFrame[] { frame } );
        response .setTotalFrames( 1l );
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<ScopesResponse> scopes( ScopesArguments args )
    {
        Scope scope = new Scope();

        ScopesResponse response = new ScopesResponse();
        response .setScopes( new Scope[] {} );
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<Void> next( NextArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "next() args: " + args .toString() );

            this .docController .actionPerformed( this, "redo" );
            // If we don't send a stopped event, the debugger will assume
            //  the "program" is running, and won't offer the step controls.
            StoppedEventArguments arguments = new StoppedEventArguments();
            arguments .setReason( "step" );
            arguments .setThreadId( THREAD_ID );
            this .client .stopped( arguments );
        } );
    }

    @Override
    public CompletableFuture<Void> stepIn( StepInArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "stepIn() args: " + args .toString() );

            this .docController .actionPerformed( this, "redo" );
            // If we don't send a stopped event, the debugger will assume
            //  the "program" is running, and won't offer the step controls.
            StoppedEventArguments arguments = new StoppedEventArguments();
            arguments .setReason( "step" );
            arguments .setThreadId( THREAD_ID );
            this .client .stopped( arguments );
        } );
    }

    @Override
    public CompletableFuture<Void> configurationDone(ConfigurationDoneArguments args)
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "configurationDone() args: " + args .toString() );
        } );
    }

    @Override
    public CompletableFuture<SetBreakpointsResponse> setBreakpoints( SetBreakpointsArguments args )
    {
        // TODO Auto-generated method stub
        return IDebugProtocolServer.super.setBreakpoints(args);
    }

    @Override
    public CompletableFuture<ContinueResponse> continue_(ContinueArguments args)
    {
        ContinueResponse response = new ContinueResponse();
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<Void> pause( PauseArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "pause() args: " + args .toString() );
        } );
    }

    @Override
    public CompletableFuture<Void> setExceptionBreakpoints( SetExceptionBreakpointsArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            if ( logger .isLoggable( Level.INFO ) )
                logger .info( "setExceptionBreakpoints() args: " + args .toString() );
        } );
    }
}
