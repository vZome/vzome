package com.vzome.dap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.debug.Breakpoint;
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
    private static final Logger LOGGER = Logger .getLogger( "com.vzome.dap.DapAdapter" );
    
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
                            showMsg( "\nWaiting for debug connection on port " + port + "..." );
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

    private void logArgs(String method, Object args) {
        if ( LOGGER .isLoggable( Level.INFO ) ) {
            LOGGER .info( method + " args: " + (args == null ? "" : args.toString() ) );
        }
    }

    private void logResponse(String method, Object response) {
        if ( LOGGER .isLoggable( Level.INFO ) ) {
            LOGGER .info( method + " response: " + (response == null ? "" : response.toString() ) );
        }
    }

    private void stopped(String method, String reason) {
        // If we don't send a stopped event, the debugger will assume
        //  the "program" is running, and won't offer the step controls.
        StoppedEventArguments arguments = new StoppedEventArguments();
        arguments .setReason( reason );
        arguments .setThreadId( THREAD_ID );
        
        logResponse( method + " --> stopped()", arguments );
        this .client .stopped( arguments );
    }

    @Override
    public CompletableFuture<Capabilities> initialize( InitializeRequestArguments args )
    {
//        logArgs( "initialize() ", args );

        Capabilities capabilities = new Capabilities();
        capabilities .setSupportsConfigurationDoneRequest( false );
        capabilities .setSupportsExceptionOptions( false );
        capabilities .setSupportsFunctionBreakpoints( false );
        
        logResponse("initialize() ", capabilities);
        return CompletableFuture .completedFuture( capabilities );
    }

    @Override
    public CompletableFuture<Void> launch( Map<String, Object> args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "launch()", args );
            
            String path = (String) args .get( "modelFile" );
            File file = new File( path );
            path = file.getAbsolutePath().toString(); // adjust path delimiters for OS as needed
            this .source .setPath( path );
            this .source .setName( file .getName() );
            this .source .setAdapterData( "vzome-adapter-data" );

            boolean stopOnEntry = (Boolean) args .get( "stopOnEntry" );
            
            this .appController .doFileAction( "openDeferringRedo", file );
            
            this .client .initialized();

            this .docController = this .appController .getSubController( path ) .getSubController( "undoRedo" );
            if ( ! stopOnEntry ) {
                this .docController .actionPerformed( this, "redoToBreakpoint" );
            }

            // If we don't send a stopped event, the debugger will assume
            //  the "program" is running, and won't offer the step controls.
            stopped( "launch()", stopOnEntry? "entry" : "breakpoint" );
        } );
    }

    @Override
    public CompletableFuture<Void> disconnect( DisconnectArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "disconnect()", args );
            
//            this .docController .actionPerformed( this, "close" ); // TODO: implement the right mechanism
        } );
    }

    @Override
    public CompletableFuture<ThreadsResponse> threads()
    {
//        logArgs( "threads()", null );

        // We have to implement this request, since it will be made whenever
        //  the debugger gets a stopped event.  Ditto stackTrace.
        org.eclipse.lsp4j.debug.Thread thread = new org.eclipse.lsp4j.debug.Thread();
        thread .setId( THREAD_ID );
        thread .setName( "SingleThread" );
        
        ThreadsResponse response = new ThreadsResponse();
        response .setThreads( new org.eclipse.lsp4j.debug.Thread[] { thread } );
        
//        logResponse("threads()", response);
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<StackTraceResponse> stackTrace( StackTraceArguments args )
    {
        logArgs( "stackTrace()", args );

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
        
        logResponse("stackTrace()", response);
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<ScopesResponse> scopes( ScopesArguments args )
    {
//        logArgs( "scopes()", args );
        
        Scope scope = new Scope();

        ScopesResponse response = new ScopesResponse();
        response .setScopes( new Scope[] {} );
        
//        logResponse("scopes()", response);
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<Void> next( NextArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "next()", args );
            
            this .docController .actionPerformed( this, "redo" );
            // If we don't send a stopped event, the debugger will assume
            //  the "program" is running, and won't offer the step controls.
            stopped( "next()", "step" );
        } );
    }

    @Override
    public CompletableFuture<Void> stepIn( StepInArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "stepIn()", args );
            
            this .docController .actionPerformed( this, "redo" );
            // If we don't send a stopped event, the debugger will assume
            //  the "program" is running, and won't offer the step controls.
            stopped( "stepIn()", "step" );
        } );
    }

    @Override
    public CompletableFuture<Void> configurationDone(ConfigurationDoneArguments args)
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "configurationDone()", args );
        } );
    }

    @Override
    public CompletableFuture<SetBreakpointsResponse> setBreakpoints( SetBreakpointsArguments args )
    {
        logArgs( "setBreakpoints()", args );
        
        String breakpoints = Arrays .asList( args .getBreakpoints() ) .stream() .map( x -> x.getLine() .toString() )
            .collect( Collectors .joining( "," ) );

        this .docController .actionPerformed( this, "setBreakpoints." + breakpoints );
        breakpoints = this .docController .getProperty( "breakpoints" );
        List<Breakpoint> brkptInts = Arrays .asList( breakpoints .split( "," ) ) .stream()
                .map( x -> {
                    int line = Integer .parseInt( x );
                    Breakpoint breakpoint = new Breakpoint();
                    breakpoint .setLine( (long) line );
                    breakpoint .setVerified( line >= 0 );
                    breakpoint .setSource( source );
                    return breakpoint;
                } ) .collect( Collectors .toList() );

        SetBreakpointsResponse response = new SetBreakpointsResponse();
        response .setBreakpoints( brkptInts.toArray( new Breakpoint[brkptInts.size()] ) );
        
        logResponse("setBreakpoints()", response);
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<ContinueResponse> continue_(ContinueArguments args)
    {
        logArgs( "continue_()", args );

        this .docController .actionPerformed( this, "redoToBreakpoint" );

        stopped( "continue_()", "breakpoint" );

        ContinueResponse response = new ContinueResponse();
        
        logResponse("continue_()", response);
        return CompletableFuture .completedFuture( response );
    }

    @Override
    public CompletableFuture<Void> pause( PauseArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "pause()", args );
        } );
    }

    @Override
    public CompletableFuture<Void> setExceptionBreakpoints( SetExceptionBreakpointsArguments args )
    {
        return CompletableFuture.runAsync( () -> {
            logArgs( "setExceptionBreakpoints()", args );
        } );
    }
}
