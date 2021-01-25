package org.hisp.dhis.webapi.controller;



import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSettingKey;
import org.hisp.dhis.user.UserSettingService;
import org.hisp.dhis.util.ObjectUtils;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Lars Helge Overland
 */
@RestController
@RequestMapping( "/userSettings" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class UserSettingController
{
    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrentUserService currentUserService;

    private static final Set<UserSettingKey> USER_SETTING_KEYS = Sets.newHashSet(
        UserSettingKey.values() ).stream().collect( Collectors.toSet() );

    // -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------

    @GetMapping
    public Map<String, Serializable> getAllUserSettings(
        @RequestParam( required = false, defaultValue = "true" ) boolean useFallback,
        @RequestParam( value = "user", required = false ) String username,
        @RequestParam( value = "userId", required = false ) String userId,
        @RequestParam( value = "key", required = false ) Set<String> keys, HttpServletResponse response )
        throws WebMessageException
    {
        User user = getUser( userId, username );

        response.setHeader( ContextUtils.HEADER_CACHE_CONTROL, CacheControl.noCache().cachePrivate().getHeaderValue() );

        if ( keys == null )
        {
            return userSettingService.getUserSettingsWithFallbackByUserAsMap( user, USER_SETTING_KEYS, useFallback );
        }

        Map<String, Serializable> result = new HashMap<>();

        for ( String key : keys )
        {
            UserSettingKey userSettingKey = getUserSettingKey( key );
            result.put( userSettingKey.getName(), userSettingService.getUserSetting( userSettingKey ) );
        }

        return result;
    }

    @GetMapping( value = "/{key}" )
    public String getUserSettingByKey(
        @PathVariable( value = "key" ) String key,
        @RequestParam( required = false, defaultValue = "true" ) boolean useFallback,
        @RequestParam( value = "user", required = false ) String username,
        @RequestParam( value = "userId", required = false ) String userId, HttpServletResponse response )
        throws WebMessageException
    {
        UserSettingKey userSettingKey = getUserSettingKey( key );
        User user = getUser( userId, username );

        Serializable value = userSettingService
            .getUserSettingsWithFallbackByUserAsMap( user, Sets.newHashSet( userSettingKey ), useFallback )
            .get( key );

        response.setHeader( ContextUtils.HEADER_CACHE_CONTROL, CacheControl.noCache().cachePrivate().getHeaderValue() );
        return String.valueOf( value );
    }

    @PostMapping( value = "/{key}" )
    public WebMessage setUserSettingByKey(
        @PathVariable( value = "key" ) String key,
        @RequestParam( value = "user", required = false ) String username,
        @RequestParam( value = "userId", required = false ) String userId,
        @RequestParam( required = false ) String value,
        @RequestBody( required = false ) String valuePayload )
        throws WebMessageException
    {
        UserSettingKey userSettingKey = getUserSettingKey( key );
        User user = getUser( userId, username );

        String newValue = ObjectUtils.firstNonNull( value, valuePayload );

        if ( StringUtils.isEmpty( newValue ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "You need to specify a new value" ) );
        }

        userSettingService.saveUserSetting( userSettingKey, UserSettingKey.getAsRealClass( key, newValue ), user );

        return WebMessageUtils.ok( "User setting saved" );
    }

    @DeleteMapping( value = "/{key}" )
    public void deleteUserSettingByKey(
        @PathVariable( value = "key" ) String key,
        @RequestParam( value = "user", required = false ) String username,
        @RequestParam( value = "userId", required = false ) String userId )
        throws WebMessageException
    {
        UserSettingKey userSettingKey = getUserSettingKey( key );
        User user = getUser( userId, username );

        userSettingService.deleteUserSetting( userSettingKey, user );
    }

    /**
     * Attempts to resolve the UserSettingKey based on the name (key) supplied
     *
     * @param key the name of a UserSettingKey
     * @return the UserSettingKey
     * @throws WebMessageException throws an exception if no UserSettingKey was found
     */
    private UserSettingKey getUserSettingKey( String key )
        throws WebMessageException
    {
        Optional<UserSettingKey> userSettingKey = UserSettingKey.getByName( key );

        if ( !userSettingKey.isPresent() )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "No user setting found with key: " + key ) );
        }

        return userSettingKey.get();
    }

    /**
     * Tries to find a user based on the uid or username. If none is supplied, currentUser will be returned.
     * If uid or username is found, it will also make sure the current user has access to the user.
     *
     * @param uid      the user uid
     * @param username the user username
     * @return the user found with uid or username, or current user if no uid or username was specified
     * @throws WebMessageException throws an exception if user was not found, or current user don't have access
     */
    private User getUser( String uid, String username )
        throws WebMessageException
    {
        User currentUser = currentUserService.getCurrentUser();
        User user;

        if ( uid == null && username == null )
        {
            return currentUser;
        }

        if ( uid != null )
        {
            user = userService.getUser( uid );
        }
        else
        {
            user = userService.getUserCredentialsByUsername( username ).getUserInfo();
        }

        if ( user == null )
        {
            throw new WebMessageException( WebMessageUtils
                .conflict( "Could not find user '" + ObjectUtils.firstNonNull( uid, username ) + "'" ) );
        }
        else
        {
            Set<String> userGroups = user.getGroups().stream().map( UserGroup::getUid ).collect( Collectors.toSet() );

            if ( !userService.canAddOrUpdateUser( userGroups ) &&
                !currentUser.getUserCredentials().canModifyUser( user.getUserCredentials() ) )
            {
                throw new WebMessageException(
                    WebMessageUtils.unathorized( "You are not authorized to access user: " + user.getUsername() ) );
            }
        }

        return user;
    }
}
