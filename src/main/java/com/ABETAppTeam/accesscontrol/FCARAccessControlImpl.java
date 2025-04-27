package com.ABETAppTeam.accesscontrol;

import com.ABETAppTeam.model.User;
import java.util.Set;

/**
 * Implementation of FCARFieldAccessControl that enforces access rules based on user roles.
 * Some fields are admin-only, while others can be edited by both admins and professors.
 */
public class FCARAccessControlImpl implements FCARFieldAccessControl {
    
    // Fields that only admins can edit
    private static final Set<String> ADMIN_ONLY_FIELDS = Set.of(
        "courseCode", 
        "semester", 
        "year",
        "outcomeId",
        "indicatorId",
        "goalId",
        "studentExpectId"
    );
    
    // Fields that both admins and professors can edit
    private static final Set<String> SHARED_FIELDS = Set.of(
        "methodDesc",
        "summaryDesc",
        "status",
        "studentOutcomes",
        "assessmentMethods",
        "improvementActions"
    );
    
    /**
     * Checks if the user can edit the specified field based on their role.
     * 
     * @param fieldName The name of the field to check
     * @param user The user requesting to edit
     * @return true if the user has permission to edit the field
     */
    @Override
    public boolean canEdit(String fieldName, User user) {
        if (user == null) {
            return false;
        }
        
        // Check if the user has the ADMIN role (using roleId or roleName)
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRoleName()) || user.getRoleId() == 1; // Assuming roleId 1 is for Admin
        
        if (isAdmin) {
            // Admins can edit all fields
            return true;
        } else if ("PROFESSOR".equalsIgnoreCase(user.getRoleName()) || user.getRoleId() == 2) { // Assuming roleId 2 is for Professor
            // Professors can only edit shared fields
            return SHARED_FIELDS.contains(fieldName);
        }
        
        return false;
    }
}