package com.example;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.BandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DefaultCubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;

public class PentahoServlet extends HttpServlet 
{

  private static final long serialVersionUID = 1L;

  @Override
  public void init(
    ServletConfig config) 
    throws ServletException 
  {

    super.init(config);
    ClassicEngineBoot.getInstance().start();

  }

  @Override
  public void doGet(
    HttpServletRequest request, 
    HttpServletResponse response)
    throws ServletException, IOException 
  {
    doPost(request, response);
  }
 
  @Override
  public void doPost(
    HttpServletRequest request, 
    HttpServletResponse response)
    throws ServletException, IOException 
  {

    try {

      // Declaring a report.
      MasterReport report = new MasterReport();

      // Data source provider.
      DriverDataSourceProvider dsProvider = new DriverDataSourceProvider();
      dsProvider.setDriver("org.hsqldb.jdbcDriver");
      dsProvider.setProperty("user", "pentaho_user");
      dsProvider.setProperty("password", "password");
      dsProvider.setUrl("jdbc:hsqldb:./resources/sampledata/sampledata");

      // Mondrian cube provider.
      DefaultCubeFileProvider cubeProvider = new DefaultCubeFileProvider();
      cubeProvider.setMondrianCubeFile("./resources/steelwheels.mondrian.xml");

      // Loading Mondrian data source.
      BandedMDXDataFactory factory = new BandedMDXDataFactory();
      factory.setDataSourceProvider(dsProvider);
      factory.setCubeFileProvider(cubeProvider);
      factory.setQuery("default", "SELECT CrossJoin([Markets].Children, [Time].Children) ON ROWS, [Measures].[Sales] ON COLUMNS FROM [SteelWheelsSales]");
      report.setDataFactory(factory);

      // Defining the crosstab.
      CrosstabGroup crosstabGroup = new CrosstabGroup(); 

      // Defining the crosstab rows.
      CrosstabRowGroupBody rowBody = (CrosstabRowGroupBody) crosstabGroup.getBody(); 
      CrosstabRowGroup rowGroup = rowBody.getGroup();
      rowGroup.setField( "[Markets].[Territory]" );
      rowGroup.getTitleHeader().addElement( createDataItem( "[Markets].[Territory]" ) ); 
      rowGroup.getHeader().addElement( createFieldItem( "[Markets].[Territory]" ) ); 
   
      // Defining the crosstab columns.
      CrosstabColumnGroupBody columnGroupBody = (CrosstabColumnGroupBody) rowGroup.getBody(); 
      CrosstabColumnGroup columnGroup = columnGroupBody.getGroup(); 
      columnGroup.setField( "[Time].[Years]" ); 
      columnGroup.getTitleHeader().addElement( createDataItem( "[Time].[Years]" ) ); 
      columnGroup.getHeader().addElement( createFieldItem( "[Time].[Years]" ) ); 

      // Defining the crosstab details.
      CrosstabCellBody body = (CrosstabCellBody) columnGroup.getBody(); 
      CrosstabCell cell = new CrosstabCell(); 
      cell.addElement( createFieldItem( "[Measures].[Sales]" ) ); 
      body.addElement( cell );

      report.setRootGroup( crosstabGroup );

      // Conversion to PDF and rendering.
      response.setContentType("application/pdf");
      PdfReportUtil.createPDF(report, response.getOutputStream());

    }
    catch (Exception e) 
    {
        e.printStackTrace();
    }
  }

  public static Element createDataItem ( 
    String text)
  { 
    Element label = new Element(); 
	label.setElementType( LabelType.INSTANCE ); 
	label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text ); 
	label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH,  100f ); 
	label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f ); 
	return label; 
  } 

  public static Element createFieldItem ( 
    String text)
  { 
	Element label = new Element(); 
	label.setElementType( TextFieldType.INSTANCE ); 
	label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, text ); 
	label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH,  100f ); 
	label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f ); 
    return label;
  } 

}

