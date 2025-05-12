package com.ABETAppTeam.accesscontrol;

import com.ABETAppTeam.model.User;

/**
 * Interface that defines the access control policy for FCAR fields.
 * Implementations of this interface determine which fields can be edited
 * by which user based on their role.
 */
public interface FCARFieldAccessControl {
    
    /**
     * Determines if a given user can edit a specific field.
     * 
     * @param fieldName The name of the field to check
     * @param user The user requesting to edit
     * @return true if the user has permission to edit the field, false otherwise
     */
    boolean canEdit(String fieldName, User user);
}