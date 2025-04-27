package com.ABETAppTeam.model;

/**
 * Model class for Department
 * This class represents an academic department in the ABET Assessment Application
 */
public class Department {
    private int id;
    private String name;

    /**
     * Default constructor
     */
    public Department() {
    }

    /**
     * Constructor with parameters
     *
     * @param id Department ID
     * @param name Department name
     */
    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get the department ID
     *
     * @return Department ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the department ID
     *
     * @param id Department ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the department name
     *
     * @return Department name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the department name
     *
     * @param name Department name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Department [id=" + id + ", name=" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Department other = (Department) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return 31 * id;
    }
}