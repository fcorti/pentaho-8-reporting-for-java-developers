package com.example;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ResourceLabelElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;


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

      // Defining the connection provider.
      DriverConnectionProvider provider = new DriverConnectionProvider();
      provider.setDriver("org.hsqldb.jdbcDriver");
      provider.setProperty("user", "pentaho_user");
      provider.setProperty("password", "password");
      provider.setUrl("jdbc:hsqldb:./resources/sampledata/sampledata");

      // Defining the queries.
      SQLReportDataFactory dataFactory = new SQLReportDataFactory(provider);
      dataFactory.setQuery("default", "SELECT ORDERNUMBER, ORDERDATE FROM ORDERS LIMIT 10");
      report.setDataFactory(dataFactory);
      report.setQuery("default");

      // Getting the Report header to host the elements.
      ReportHeader reportHeader = report.getReportHeader();

      // Defining the default locale.
      Locale locale = new Locale("es");
      // Locale locale = new Locale("en");
      Locale.setDefault(locale);

      // Adding a resource label to the report header band.
      ResourceLabelElementFactory labelFactory = new ResourceLabelElementFactory();
      labelFactory.setResourceKey("ORDERNUMBER");
      labelFactory.setResourceBase("my-first-reporting-project");
      labelFactory.setBold(true);
      labelFactory.setX(1f);
      labelFactory.setY(1f);
      labelFactory.setMinimumWidth(100f);
      labelFactory.setMinimumHeight(20f);
      labelFactory.setBold(true);
      Element labelField = labelFactory.createElement();
      reportHeader.addElement(labelField);
      
      // Adding a resource label to the report header band.
      ResourceLabelElementFactory label2Factory = new ResourceLabelElementFactory();
      label2Factory.setResourceKey("ORDERDATE");
      label2Factory.setResourceBase("my-first-reporting-project");
      label2Factory.setBold(true);
      label2Factory.setX(101f);
      label2Factory.setY(1f);
      label2Factory.setMinimumWidth(200f);
      label2Factory.setMinimumHeight(20f);
      label2Factory.setBold(true);
      Element label2Field = label2Factory.createElement();
      reportHeader.addElement(label2Field);

      // Getting the item band to host the elements.
      ItemBand itemBand = report.getItemBand();

      // Adding a field to the details band.
      NumberFieldElementFactory numberFactory = new NumberFieldElementFactory();
      numberFactory.setFieldname("ORDERNUMBER");
      numberFactory.setX(1f);
      numberFactory.setY(1f);
      numberFactory.setMinimumWidth(100f);
      numberFactory.setMinimumHeight(20f);
      Element numberField = numberFactory.createElement();
      itemBand.addElement(numberField);

      // Adding a field to the details band.
      DateFieldElementFactory dateFactory = new DateFieldElementFactory();
      dateFactory.setFieldname("ORDERDATE");
      dateFactory.setX(101f);
      dateFactory.setY(1f);
      dateFactory.setMinimumWidth(200f);
      dateFactory.setMinimumHeight(20f);
      Element dateField = dateFactory.createElement();
      itemBand.addElement(dateField);

      // Conversion to PDF and rendering.
      response.setContentType("application/pdf");
      PdfReportUtil.createPDF(report, response.getOutputStream());

    }
    catch (Exception e) 
    {
        e.printStackTrace();
    }
  }
}

