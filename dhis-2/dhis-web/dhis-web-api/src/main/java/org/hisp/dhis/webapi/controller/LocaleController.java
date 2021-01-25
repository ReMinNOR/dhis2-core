package org.hisp.dhis.webapi.controller;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.i18n.I18nLocaleService;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping( value = "/locales" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class LocaleController
{
    @Autowired
    private LocaleManager localeManager;

    @Autowired
    private ContextService contextService;

    @Autowired
    private I18nLocaleService localeService;

    @Autowired
    private WebMessageService webMessageService;

    // -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------

    @GetMapping( value = "/ui" )
    public @ResponseBody List<WebLocale> getUiLocales( Model model )
    {
        List<Locale> locales = localeManager.getAvailableLocales();
        List<WebLocale> webLocales = locales.stream().map( WebLocale::fromLocale ).collect( Collectors.toList() );

        return webLocales;
    }

    @GetMapping( value = "/db" )
    public @ResponseBody List<WebLocale> getDbLocales()
    {
        List<Locale> locales = localeService.getAllLocales();
        List<WebLocale> webLocales = locales.stream().map( WebLocale::fromLocale ).collect( Collectors.toList() );
        return webLocales;
    }

    @GetMapping( value = "/languages", produces = "application/json" )
    public @ResponseBody Map<String, String> getAvailableLanguages()
    {
        return localeService.getAvailableLanguages();
    }

    @GetMapping( value = "/countries", produces = "application/json" )
    public @ResponseBody Map<String, String> getAvailableCountries()
    {
        return localeService.getAvailableCountries();
    }

    @GetMapping( value = "/dbLocales", produces = "application/json" )
    public @ResponseBody List<I18nLocale> getDbLocalesWithId()
    {
        return localeService.getAllI18nLocales();
    }

    @GetMapping( value = "/dbLocales/{uid}", produces = "application/json")
    public @ResponseBody I18nLocale getObject( @PathVariable( "uid" ) String uid, HttpServletResponse response ) throws Exception
    {
        response.setHeader( ContextUtils.HEADER_CACHE_CONTROL, CacheControl.noCache().cachePrivate().getHeaderValue() );
        I18nLocale locale = localeService.getI18nLocaleByUid( uid );

        if ( locale == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "Cannot find Locale with uid: " + uid ) );
        }

        return locale;
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_LOCALE_ADD')" )
    @PostMapping( value="/dbLocales" )
    @ResponseStatus( value = HttpStatus.OK )
    public void addLocale( @RequestParam String country, @RequestParam String language,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        if ( StringUtils.isEmpty( country ) || StringUtils.isEmpty( language ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Invalid country or language code." ) );
        }

        String localeCode = LocaleUtils.getLocaleString( language, country, null );

        Locale locale = LocaleUtils.getLocale( localeCode );

        if ( locale != null )
        {
            I18nLocale i18nLocale = localeService.getI18nLocale( locale );

            if ( i18nLocale != null )
            {
                throw new WebMessageException( WebMessageUtils.conflict( "Locale code existed." ) );
            }
        }

        I18nLocale i18nLocale = localeService.addI18nLocale( language, country );

        WebMessage webMessage = WebMessageUtils.created( "Locale created successfully" );

        response.setHeader( ContextUtils.HEADER_LOCATION, contextService.getApiPath() + "/locales/" + i18nLocale.getUid() );

        webMessageService.send( webMessage, response, request );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_LOCALE_DELETE')" )
    @DeleteMapping( path = "/dbLocales/{uid}" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void delete( @PathVariable String uid ) throws Exception
    {
        I18nLocale i18nLocale = localeService.getI18nLocaleByUid( uid );

        if ( i18nLocale == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "Cannot find Locale with uid " + uid ) );
        }

        localeService.deleteI18nLocale( i18nLocale );
    }
}
