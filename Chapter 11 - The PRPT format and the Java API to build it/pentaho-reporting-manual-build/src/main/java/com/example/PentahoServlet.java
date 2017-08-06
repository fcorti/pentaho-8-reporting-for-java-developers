package com.example;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.DefaultTableModel;

import org.pentaho.plugin.jfreereport.reportcharts.BarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.CategorySetDataCollector;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;

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

      // Define a simple TableModel.
      DefaultTableModel tableModel = new DefaultTableModel (
        new Object[][]
        {
          {"Line One", "Product One",   10, 100},
          {"Line One", "Product Two",   20, 200},
          {"Line Two", "Product Three", 30, 300},
          {"Line Two", "Product Four",  40, 400},
          {"Line Two", "Product Five",  50, 500},
        },
        new String[] {"Line", "Product", "Cost", "Quantity"}
      );

      // Create a TableDataFactory.
      final TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable("default", tableModel);

      // Add the factory to the report.
      report.setDataFactory(dataFactory);

      // Create a formula expression.
      FormulaExpression formula = new FormulaExpression();

      // Configure the formulas properties.
      formula.setName("totalCost");
      formula.setFormula("=[Cost]*[Quantity]");
      report.addExpression(formula);

      // Creating report header object.
      ReportHeader reportHeader = new ReportHeader();
      report.setReportHeader(reportHeader);

      // Defining a data collector for the chart.
      CategorySetDataCollector collector = new CategorySetDataCollector();
      collector.setName("CategorySetCollectorFunction");
      collector.setSeriesColumn(0, "Cost");
      collector.setSeriesName(0, "");
      collector.setValueColumn(0, "Cost");
      collector.setCategoryColumn("Product");
      collector.setAutoGenerateMissingSeriesNames(false);

      // Chart layout.
      BarChartExpression chartExpression = new BarChartExpression();
      chartExpression.setShowLegend(false);
      chartExpression.setSeriesColor(
        new String[] {"#abcd37","#0392ce","#f9bc02","#66b033","#cc0099"}
      );

      // Declaring an element for the chart.
      Element chartElement = new Element();
      chartElement.setElementType(new LegacyChartType());
      chartElement.setName("MySuperCoolChartFromApi");
      chartElement.setAttributeExpression(
        AttributeNames.Core.NAMESPACE,
        AttributeNames.Core.VALUE, 
        chartExpression);
      chartElement.setAttribute(
        LegacyChartElementModule.NAMESPACE,
        LegacyChartElementModule.PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE,
        collector);
      chartElement.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 200F);
      chartElement.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 500F);

      // Adding the chart to the report header.
      reportHeader.addElement(chartElement);

      // Creating a label element factory instance.
      LabelElementFactory factory = new LabelElementFactory();

      // Configuring the label's text.
      factory.setText("Product");
      factory.setX(1f);
      factory.setY(250f);
      factory.setMinimumWidth(100f);
      factory.setMinimumHeight(20f);
      factory.setBold(true);

      // Instantiate the label element.
      Element label1 = factory.createElement();
      reportHeader.addElement(label1);

      // Configuring the label's text.
      factory.setText("Total cost");
      factory.setX(101f);
      factory.setY(250f);
      factory.setMinimumWidth(100f);
      factory.setMinimumHeight(20f);
      factory.setBold(true);

      // Instantiate the label element.
      Element label2 = factory.createElement();
      reportHeader.addElement(label2);

      // Getting the item band to host the elements.
      ItemBand itemBand = report.getItemBand();

      // Adding a text field for the product name, added to the item band.
      TextFieldElementFactory textFactory = new TextFieldElementFactory(); 
      textFactory.setFieldname("Product");
      textFactory.setX(1f);
      textFactory.setY(1f); 
      textFactory.setMinimumWidth(100f);
      textFactory.setMinimumHeight(20f);
      Element nameField = textFactory.createElement(); 
      itemBand.addElement(nameField);

      // Adding a number filed with the total cost of the products.
      NumberFieldElementFactory numberFactory = new NumberFieldElementFactory();
      numberFactory.setFieldname("totalCost");
      numberFactory.setX(101f);
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


