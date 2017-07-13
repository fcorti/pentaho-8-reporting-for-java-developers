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
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;

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

      // Loading MQL data source.
      PmdDataFactory factory = new PmdDataFactory();
      factory.setConnectionProvider(new PmdConnectionProvider());
      factory.setXmiFile("resources/metadata.xmi");
      factory.setDomainId("test");
      factory.setQuery(
        "default",
        "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<mql>" + 
          "<domain_id>test</domain_id>" + 
          "<model_id>BV_ORDERS</model_id>" + 
          "<options>" + 
            "<disable_distinct>false</disable_distinct>" + 
            "<limit>-1</limit>" + 
          "</options>" + 
          "<selections>" + 
            "<selection>" + 
              "<view>CAT_PRODUCTS</view>" + 
              "<column>BC_PRODUCTS_PRODUCTNAME</column>" + 
              "<aggregation>NONE</aggregation>" + 
            "</selection>" + 
            "<selection>" + 
              "<view>CAT_PRODUCTS</view>" + 
              "<column>BC_PRODUCTS_MSRP</column>" + 
              "<aggregation>AVERAGE</aggregation>" + 
            "</selection>" + 
          "</selections>" + 
          "<constraints/>" + 
          "<orders/>" + 
        "</mql>",
        null,
        null);

      report.setDataFactory(factory);

      // Getting the item band to host the elements.
      ItemBand itemBand = report.getItemBand();

      // Adding a text field for the product name, added to the item band.
      TextFieldElementFactory textFactory = new TextFieldElementFactory(); 
      textFactory.setFieldname("BC_PRODUCTS_PRODUCTNAME");
      textFactory.setX(1f);
      textFactory.setY(1f); 
      textFactory.setMinimumWidth(200f);
      textFactory.setMinimumHeight(20f);
      Element nameField = textFactory.createElement(); 
      itemBand.addElement(nameField);

      // Adding a number filed with the total cost of the products.
      NumberFieldElementFactory numberFactory = new NumberFieldElementFactory();
      numberFactory.setFieldname("BC_PRODUCTS_MSRP");
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

