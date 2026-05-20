

public class Employee extends User {
    private final String employeeId;
    private String position;

    public Employee(String userId, String username, String password,
                    String name, String email, String employeeId, String position) {
        super(userId, username, password, name, email);
        this.employeeId = employeeId;
        this.position = position;
    }


    public String getEmployeeId() {
        return employeeId;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
}







