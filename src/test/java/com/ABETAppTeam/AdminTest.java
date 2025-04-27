package com.ABETAppTeam;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AdminTest {

    @Test
    void testAddPermissionSuccessfullyAddsNewPermission() {
        // Arrange
        Admin admin = new Admin();
        String permission = "NEW_PERMISSION";

        // Act
        admin.addPermission(permission);

        // Assert
        assertTrue(admin.getPermissions().contains(permission), "Permission should be added successfully.");
    }

    @Test
    void testAddPermissionDoesNotAddDuplicatePermission() {
        // Arrange
        Admin admin = new Admin();
        String permission = "DUPLICATE_PERMISSION";
        admin.addPermission(permission);

        // Act
        admin.addPermission(permission);

        // Assert
        long count = admin.getPermissions().stream().filter(p -> p.equals(permission)).count();
        assertEquals(1, count, "Duplicate permissions should not be added.");
    }

    @Test
    void testAddPermissionWithNull() {
        // Arrange
        Admin admin = new Admin();

        // Act
        admin.addPermission(null);

        // Assert
        assertFalse(admin.getPermissions().contains(null), "Null should not be added as a permission.");
    }

    @Test
    void testAddPermissionWithEmptyString() {
        // Arrange
        Admin admin = new Admin();

        // Act
        admin.addPermission("");

        // Assert
        assertFalse(admin.getPermissions().contains(""), "Empty string should not be added as a permission.");
    }

    @Test
    void testSetPermissionsWithValidInput() {
        // Arrange
        Admin admin = new Admin();
        List<String> newPermissions = List.of("PERMISSION_1", "PERMISSION_2", "PERMISSION_3");

        // Act
        admin.setPermissions(new ArrayList<>(newPermissions));

        // Assert
        assertEquals(newPermissions, admin.getPermissions(), "Admin's permissions should match the provided valid list.");
    }

    @Test
    void testSetPermissionsWithEmptyList() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("PERMISSION_1");

        // Act
        admin.setPermissions(new ArrayList<>());

        // Assert
        assertTrue(admin.getPermissions().isEmpty(), "Admin's permissions list should be cleared when set to an empty list.");
    }

    @Test
    void testSetPermissionsWithNull() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("PERMISSION_1");

        // Act
        admin.setPermissions(null);

        // Assert
        assertNull(admin.getPermissions(), "Admin's permissions list should be null when set to null.");
    }

    @Test
    void testGetPermissionsDefault() {
        // Arrange
        Admin admin = new Admin();

        // Act
        List<String> permissions = admin.getPermissions();

        // Assert
        assertNotNull(permissions, "Permissions list should not be null.");
        assertTrue(permissions.isEmpty(), "Default permissions list should be empty.");
    }

    @Test
    void testGetPermissionsAfterSetPermissions() {
        // Arrange
        Admin admin = new Admin();
        List<String> newPermissions = List.of("PERM_1", "PERM_2", "PERM_3");
        admin.setPermissions(new ArrayList<>(newPermissions));

        // Act
        List<String> permissions = admin.getPermissions();

        // Assert
        assertEquals(newPermissions, permissions, "Permissions list should match the newly set list.");
    }

    @Test
    void testGetPermissionsImmutability() {
        // Arrange
        Admin admin = new Admin();
        List<String> newPermissions = List.of("PERM_1", "PERM_2");
        admin.setPermissions(new ArrayList<>(newPermissions));

        // Act
        List<String> returnedPermissions = admin.getPermissions();
        returnedPermissions.add("PERM_3"); // Modify the returned list.

        // Assert
        assertNotEquals(returnedPermissions, admin.getPermissions(),
                "Modifying the returned list should not affect the Admin's internal permissions list.");
    }

    @Test
    void testGetPermissionsAfterAddPermission() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("NEW_PERMISSION");

        // Act
        List<String> permissions = admin.getPermissions();

        // Assert
        assertTrue(permissions.contains("NEW_PERMISSION"),
                "Permissions list should include the newly added permission.");
    }

    @Test
    void testGetPermissionsAfterRemovePermission() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("TO_REMOVE");
        admin.removePermission("TO_REMOVE");

        // Act
        List<String> permissions = admin.getPermissions();

        // Assert
        assertFalse(permissions.contains("TO_REMOVE"),
                "Permissions list should not include the removed permission.");
    }

    /**
     * The AdminTest class is responsible for testing the behavior of the Admin class, specifically the `hasPermission` method.
     * <p>
     * Method being tested:
     * - `hasPermission(String permission)`: Checks if the Admin object has the specified permission in its permissions list.
     */

    @Test
    void testHasPermissionWithMixedCasePermissions() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("Manage_Users"); // Mixed case added

        // Act
        boolean result = admin.hasPermission("MANAGE_USERS");

        // Assert
        assertFalse(result, "Admin permissions should be case-sensitive and 'MANAGE_USERS' should not match 'Manage_Users'.");
    }

    @Test
    void testHasPermissionWithUnrelatedDataInPermissions() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("UNRELATED_DATA");

        // Act
        boolean result = admin.hasPermission("MANAGE_USERS");

        // Assert
        assertFalse(result, "Admin should not have the 'MANAGE_USERS' permission if 'permissions' contains unrelated data.");
    }

    @Test
    void testHasPermissionWithSpecialCharacterPermissions() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("VIEW_REPORTS#2023!");

        // Act
        boolean result = admin.hasPermission("VIEW_REPORTS#2023!");

        // Assert
        assertTrue(result, "Admin should have permissions containing special characters, such as 'VIEW_REPORTS#2023!'.");
    }

    @Test
    void testHasPermissionWhenPermissionExists() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );

        // Act
        boolean result = admin.hasPermission("MANAGE_USERS");

        // Assert
        assertTrue(result, "Admin should have the MANAGE_USERS permission.");
    }

    @Test
    void testHasPermissionWhenPermissionDoesNotExist() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );

        // Act
        boolean result = admin.hasPermission("NON_EXISTENT_PERMISSION");

        // Assert
        assertFalse(result, "Admin should not have the NON_EXISTENT_PERMISSION.");
    }

    @Test
    void testHasPermissionWhenPermissionsListIsEmpty() {
        // Arrange
        Admin admin = new Admin();
        admin.setPermissions(List.of()); // Empty permissions list

        // Act
        boolean result = admin.hasPermission("MANAGE_USERS");

        // Assert
        assertFalse(result, "Admin should not have the MANAGE_USERS permission when permissions list is empty.");
    }

    @Test
    void testHasPermissionWithNullPermission() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );

        // Act
        boolean result = admin.hasPermission(null);

        // Assert
        assertFalse(result, "Admin should not have a null permission.");
    }

    @Test
    void testHasPermissionWithEmptyString() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );

        // Act
        boolean result = admin.hasPermission("");

        // Assert
        assertFalse(result, "Admin should not have an empty string as a permission.");
    }

    @Test
    void testHasPermissionWithCaseSensitivity() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );

        // Act
        boolean result = admin.hasPermission("manage_users"); // Different case

        // Assert
        assertFalse(result, "Admin permissions should be case-sensitive.");
    }

    @Test
    void testHasPermissionWithDuplicatePermissions() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("DUPLICATE_PERMISSION");
        admin.addPermission("DUPLICATE_PERMISSION"); // Add duplicate

        // Act
        long count = admin.getPermissions().stream().filter(p -> p.equals("DUPLICATE_PERMISSION")).count();

        // Assert
        assertEquals(1, count, "Duplicate permissions should not be added.");
    }

    @Test
    void testHasPermissionAfterAddingPermission() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("EDIT_REPORTS");

        // Act
        boolean result = admin.hasPermission("EDIT_REPORTS");

        // Assert
        assertTrue(result, "Admin should have the EDIT_REPORTS permission after it was added.");
    }

    @Test
    void testHasPermissionAfterRemovingPermission() {
        // Arrange
        Admin admin = new Admin(
                1,
                "John",
                "Doe",
                "johndoe@example.com",
                "hashedPassword123",
                1,
                1,
                true
        );
        admin.removePermission("VIEW_REPORTS");

        // Act
        boolean result = admin.hasPermission("VIEW_REPORTS");

        // Assert
        assertFalse(result, "Admin should not have the VIEW_REPORTS permission after it was removed.");
    }

    @Test
    void testRemovePermissionSuccessfullyRemovesExistingPermission() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("REMOVE_ME");

        // Act
        admin.removePermission("REMOVE_ME");

        // Assert
        assertFalse(admin.getPermissions().contains("REMOVE_ME"),
                "Permission should be successfully removed.");
    }

    @Test
    void testRemovePermissionDoesNotAffectNonexistentPermission() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("EXISTING_PERMISSION");

        // Act
        admin.removePermission("NON_EXISTENT_PERMISSION");

        // Assert
        assertEquals(1, admin.getPermissions().size(),
                "Removing a nonexistent permission should not affect existing permissions.");
        assertTrue(admin.getPermissions().contains("EXISTING_PERMISSION"),
                "Existing permission should still be present.");
    }

    @Test
    void testRemovePermissionWithNull() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("EXISTING_PERMISSION");

        // Act
        admin.removePermission(null);

        // Assert
        assertTrue(admin.getPermissions().contains("EXISTING_PERMISSION"),
                "Null removal should not affect the permissions list.");
        assertEquals(1, admin.getPermissions().size(),
                "Permissions list size should remain unaffected by null removal.");
    }

    @Test
    void testRemovePermissionWithEmptyString() {
        // Arrange
        Admin admin = new Admin();
        admin.addPermission("EXISTING_PERMISSION");

        // Act
        admin.removePermission("");

        // Assert
        assertTrue(admin.getPermissions().contains("EXISTING_PERMISSION"),
                "Removing an empty string should not affect the permissions list.");
        assertEquals(1, admin.getPermissions().size(),
                "Permissions list size should remain unaffected by removing an empty string.");
    }
}