package org.hisp.dhis.webapi.controller;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.commons.util.StreamUtils;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.common.ImportOptions;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormService;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormUtil;
import org.hisp.dhis.dxf2.pdfform.PdfFormFontSettings;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.DateUtils;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author James Chang <jamesbchang@gmail.com>
 */
@Controller
@RequestMapping( value = "/pdfForm" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class PdfFormController
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private PdfDataEntryFormService pdfDataEntryFormService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private WebMessageService webMessageService;

    //--------------------------------------------------------------------------
    // DataSet
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/dataSet/{dataSetUid}", method = RequestMethod.GET )
    public void getFormPdfDataSet( @PathVariable String dataSetUid, HttpServletRequest request,
        HttpServletResponse response, OutputStream out ) throws Exception
    {
        Document document = new Document();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance( document, baos );

        PdfFormFontSettings pdfFormFontSettings = new PdfFormFontSettings();

        PdfDataEntryFormUtil.setDefaultFooterOnDocument( document, request.getServerName(),
            pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_FOOTER ) );

        pdfDataEntryFormService.generatePDFDataEntryForm( document, writer, dataSetUid,
            PdfDataEntryFormUtil.DATATYPE_DATASET,
            PdfDataEntryFormUtil.getDefaultPageSize( PdfDataEntryFormUtil.DATATYPE_DATASET ),
            pdfFormFontSettings, i18nManager.getI18nFormat() );

        String fileName = dataSetService.getDataSet( dataSetUid ).getName() + " " +
            DateUtils.getMediumDateString() + ".pdf";

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.NO_CACHE, fileName, true );
        response.setContentLength( baos.size() );

        baos.writeTo( out );
    }

    @RequestMapping( value = "/dataSet", method = RequestMethod.POST )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void sendFormPdfDataSet( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        JobConfiguration jobId = new JobConfiguration( "inMemoryDataValueImport",
            JobType.DATAVALUE_IMPORT, currentUserService.getCurrentUser().getUid(), true );

        notifier.clear( jobId );

        InputStream in = request.getInputStream();

        in = StreamUtils.wrapAndCheckCompressionFormat( in );

        dataValueSetService.saveDataValueSetPdf( in, ImportOptions.getDefaultImportOptions(), jobId );

        webMessageService.send( WebMessageUtils.ok( "Import successful." ), response, request );
    }

    //--------------------------------------------------------------------------
    // Program Stage
    //--------------------------------------------------------------------------
}
