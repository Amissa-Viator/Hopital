package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.AgeGroup;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddPatient {
    @FXML
    private Button addBtn;

    @FXML
    private TextField ageFld;

    @FXML
    private ChoiceBox<String> groupBox;

    @FXML
    private TextField lastNameFld;

    @FXML
    private TextField nameFld;

    private int selectedAgeGroupId;
    private Integer id;
    private Stage stage;
    private List<AgeGroup> ageGroupList = new ArrayList<>();
    private boolean isUpdate = false;

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @FXML
    void initialize() {
        readGroups();
        groupBox.setOnAction(event -> {
            String selectedGroup = groupBox.getValue();
            if (selectedGroup != null) {
                for(AgeGroup choice: ageGroupList) {
                    if(choice.getAgeCategory().equals(selectedGroup)) {
                        selectedAgeGroupId = choice.getId();
                    }
                }
            }
        });
        addBtn.setOnAction(event -> handleInsert());
    }

    private void readGroups() {
        try {
            String sql = "SELECT * FROM age_group";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            groupBox.getItems().clear();

            // Додавання спеціалізацій до ChoiceBox
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String age_group = resultSet.getString("age_category");
                AgeGroup group = new AgeGroup(id, age_group);
                ageGroupList.add(group);
                groupBox.getItems().add(age_group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private void handleInsert() {
        String newName = nameFld.getText();
        String newSurname = lastNameFld.getText();
        int newAge = Integer.parseInt(ageFld.getText());
        if(newName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(newName, newSurname, newAge);
            } else {
                update(newName, newSurname, newAge);
            }
        }
    }

    private void insert(String name, String surname, int age) {
        String insertPatient = "INSERT INTO patients (pname, plast_name, age) VALUES (?,?,?)";
        String insertCard = "INSERT INTO card (id_patient, id_age_category) VALUES (?,?)";


        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertPatient, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, name);
            insertState.setString(2, surname);
            insertState.setInt(3, age);
            insertState.executeUpdate();

            var generatedKeys = insertState.getGeneratedKeys();
            int idPatient;
            if (generatedKeys.next()) {
                idPatient = generatedKeys.getInt("id");
            } else {
                throw new SQLException("Creating patient failed, no ID obtained.");
            }

            PreparedStatement statement = Main.CONNECTION.prepareStatement(insertCard, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, idPatient);
            statement.setInt(2, selectedAgeGroupId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    void close() {
        stage.close();
    }

    private void update(String name, String surname, int age) {
        String updatePatientQuery = "UPDATE patients SET pname=?, plast_name=?, age=? WHERE id=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updatePatientQuery);
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setInt(3, age);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String updateQuery = "UPDATE card SET id_age_category=? WHERE id_patient=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateQuery);
            statement.setInt(1,selectedAgeGroupId);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    public void setTextField(int sendId, String name, String surname, int age) {
        id = sendId;
        nameFld.setText(name);
        lastNameFld.setText(surname);
        ageFld.setText(String.valueOf(age));
    }
}
