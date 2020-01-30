package com.vzome.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.api.Exporter;

@SuppressWarnings("serial")
public class ExporterServlet extends HttpServlet
{
  Application app;
  
  public void init( ServletConfig config )
  throws ServletException
  {
      super.init( config );

      app = new Application();
  }

  public void doGet( HttpServletRequest req, HttpServletResponse res )
      throws ServletException, IOException
  {
      InputStream bytes = null;
      PrintWriter out = null;
      try {
          System .out .println( "context path is = " + req .getContextPath() );
          System .out .println( "servlet path is = " + req .getServletPath() );
          System .out .println( "original URI is = " + req .getRequestURI() );
          System .out .println( "original URL is = " + req .getRequestURL() );

          String urlStr = req .getQueryString();
          System .out .println( "URL is = " + urlStr );
          if ( urlStr == null ) {
              res .setStatus( 404 );
              // TODO return an error message
              return;
          }
          URL vZomeFile = new URL( urlStr );
          String format = req .getPathInfo();
          if ( format == null ) {
              res .setStatus( 404 );
              // TODO return an error message
              return;
          }
          format = format .substring( 1 );
          System .out .println( "format is = " + format );
          
          Exporter exporter = this .app .getExporter( format );
          if ( exporter == null || ! exporter .isValid() ) {
              res .setStatus( 404 );
              // TODO return an error message
              return;
          }

          // set header field first
          res .setContentType( exporter .getContentType() );

          out = res .getWriter();
          
          bytes = vZomeFile .openStream();
          
          Document model = app .loadDocument( bytes );
          exporter .doExport( model, out, 1080, 1920 );
          
      } catch ( Exception e ) {
          e .printStackTrace();
          throw new ServletException( e );
      } finally {
          if ( bytes != null )
              bytes .close();
          if ( out != null )
              out .close();
      }
  }

  public String getServletInfo()
  {
      return "Exports a JSON file from a vZome model, for use in vZome webview";
  }
}
