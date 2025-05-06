package com.ABETAppTeam;

import com.ABETAppTeam.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/migrate-passwords")
public class PasswordMigrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is admin
        // Add your authentication check here

        UserRepository userRepo = new UserRepository();
        int migratedCount = userRepo.migrateExistingPasswords();

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Migration complete. " + migratedCount + " passwords were migrated.");
    }
}