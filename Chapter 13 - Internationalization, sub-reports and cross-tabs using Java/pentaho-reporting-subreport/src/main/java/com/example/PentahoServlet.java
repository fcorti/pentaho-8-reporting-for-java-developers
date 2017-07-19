package com.example;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
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
      dataFactory.setQuery("default", "SELECT ORDERNUMBER, ORDERDATE FROM ORDERS");
      dataFactory.setQuery("default2", "SELECT PRODUCTCODE, QUANTITYORDERED, PRICEEACH FROM ORDERDETAILS WHERE ORDERNUMBER=${ORDERNUMBER}  ORDER BY ORDERLINENUMBER ASC LIMIT 5");
      report.setDataFactory(dataFactory);
      report.setQuery("default");

      // Getting the item band to host the elements.
      ItemBand itemBand = report.getItemBand();

      // Adding a field to the details band.
      NumberFieldElementFactory numberFactory = new NumberFieldElementFactory();
      numberFactory.setFieldname("ORDERNUMBER");
      numberFactory.setX(1f);
      numberFactory.setY(1f);
      numberFactory.setMinimumWidth(100f);
      numberFactory.setMinimumHeight(20f);
      numberFactory.setBold(true);
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

      // Creating the subreport.
      SubReport subReport = new SubReport();
      subReport.setQuery("default2");
      subReport.addInputParameter("ORDERNUMBER", "ORDERNUMBER");

      // Adding a field to the subreport's details band.
      TextFieldElementFactory textFactory2 = new TextFieldElementFactory();
      textFactory2.setFieldname("PRODUCTCODE");
      textFactory2.setX(101f);
      textFactory2.setY(1f);
      textFactory2.setMinimumWidth(200f);
      textFactory2.setMinimumHeight(20f);
      Element textField2 = textFactory2.createElement();
      subReport.getItemBand().addElement(textField2);

      // Adding a field to the subreport's details band.
      NumberFieldElementFactory numberFactory2 = new NumberFieldElementFactory();
      numberFactory2.setFieldname("QUANTITYORDERED");
      numberFactory2.setX(301f);
      numberFactory2.setY(1f);
      numberFactory2.setMinimumWidth(100f);
      numberFactory2.setMinimumHeight(20f);
      Element numberField2 = numberFactory2.createElement();
      subReport.getItemBand().addElement(numberField2);

      // Adding a field to the subreport's details band.
      NumberFieldElementFactory numberFactory3 = new NumberFieldElementFactory();
      numberFactory3.setFieldname("PRICEEACH");
      numberFactory3.setX(401f);
      numberFactory3.setY(1f);
      numberFactory3.setMinimumWidth(100f);
      numberFactory3.setMinimumHeight(20f);
      Element numberField3 = numberFactory3.createElement();
      subReport.getItemBand().addElement(numberField3);

      // Adding the subreport to the report's details band.
      itemBand.addSubReport(subReport);

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

