package com.example;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;


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

      // Loading Kettle data source.
      KettleTransFromFileProducer producer = new KettleTransFromFileProducer (
        "resources/CSV Input - Reading customer data.ktr",
        "Dummy (do nothing)",
        new FormulaArgument[0], 
        new FormulaParameter[0]);
      KettleDataFactory factory = new KettleDataFactory();
      factory.setQuery("default", producer);
      report.setDataFactory(factory);

      // Getting the item band to host the elements.
      ItemBand itemBand = report.getItemBand();

      // Adding a text field for the product name, added to the item band.
      TextFieldElementFactory textFactory = new TextFieldElementFactory(); 
      textFactory.setFieldname("name");
      textFactory.setX(1f);
      textFactory.setY(1f); 
      textFactory.setMinimumWidth(200f);
      textFactory.setMinimumHeight(20f);
      Element nameField = textFactory.createElement(); 
      itemBand.addElement(nameField);

      // Adding a number filed with the total cost of the products.
      NumberFieldElementFactory numberFactory = new NumberFieldElementFactory();
      numberFactory.setFieldname("id");
      numberFactory.setX(201f);
      numberFactory.setY(1f);
      numberFactory.setMinimumWidth(100f);
      numberFactory.setMinimumHeight(20f);
      Element totalCost = numberFactory.createElement();
      itemBand.addElement(totalCost);

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

