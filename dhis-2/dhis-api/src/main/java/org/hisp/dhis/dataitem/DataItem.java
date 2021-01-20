package org.hisp.dhis.dataitem;

/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static lombok.AccessLevel.NONE;
import static org.hisp.dhis.common.DxfNamespaces.DXF_2_0;
import static org.hisp.dhis.translation.TranslationProperty.NAME;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.UserContext;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationProperty;
import org.hisp.dhis.user.UserSettingKey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@JacksonXmlRootElement( localName = "dataItem", namespace = DXF_2_0 )
public class DataItem implements Serializable
{
    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String name;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    @Getter( value = NONE )
    @Setter( value = NONE )
    private String displayName;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String id;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String dimensionItemType;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String programId;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String combinedId;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String valueType;

    @JsonProperty
    @JacksonXmlProperty( namespace = DXF_2_0 )
    private String simplifiedValueType;

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Getter( value = NONE )
    @Setter( value = NONE )
    protected Set<Translation> translations = new HashSet<>();

    /**
     * Cache for object translations, where the cache key is a combination of
     * locale and translation property, and value is the translated value.
     */
    @Getter( value = NONE )
    @Setter( value = NONE )
    protected Map<String, String> translationCache = new HashMap<>();

    private DataItem dataItem;

    public String getDisplayName()
    {
        return getTranslation( NAME, getName() );
    }

    /**
     * Returns a translated value for this object for the given property. The
     * current locale is read from the user context.
     *
     * @param property the translation property.
     * @param defaultValue the value to use if there are no translations.
     * @return a translated value.
     */
    protected String getTranslation( TranslationProperty property, String defaultValue )
    {
        Locale locale = UserContext.getUserSetting( UserSettingKey.DB_LOCALE );

        defaultValue = defaultValue != null ? defaultValue.trim() : null;

        if ( locale == null || property == null )
        {
            return defaultValue;
        }

        loadTranslationsCacheIfEmpty();

        String cacheKey = Translation.getCacheKey( locale.toString(), property );

        return translationCache.getOrDefault( cacheKey, defaultValue );
    }

    /**
     * Populates the translationsCache map unless it is already populated.
     */
    private void loadTranslationsCacheIfEmpty()
    {
        if ( translationCache.isEmpty() && translations != null )
        {
            for ( Translation translation : translations )
            {
                if ( translation.getLocale() != null && translation.getProperty() != null
                    && !StringUtils.isEmpty( translation.getValue() ) )
                {
                    String key = Translation.getCacheKey( translation.getLocale(), translation.getProperty() );
                    translationCache.put( key, translation.getValue() );
                }
            }
        }
    }
}
