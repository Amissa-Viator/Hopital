package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import task.database.entity.DoctorDetails;
import task.database.entity.Doctors;
import task.database.entity.ExamDetails;
import task.database.entity.Patients;

import java.util.List;

public class ShowController {
    @FXML
    private VBox textBox;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public <T> void displayList(List<T> list) {
        textBox.getChildren().clear();
        String value=null;
        for (T item : list) {
            if(item instanceof Doctors) {
                value = ((Doctors) item).getName() + " " + ((Doctors) item).getLastName();
            } else if(item instanceof MedicalInfo) {
                value = ((MedicalInfo) item).getMedicine() + " :" + ((MedicalInfo) item).getCompany() + " ," + ((MedicalInfo) item).getAddress();
            } else if(item instanceof DoctorDetails) {
                value = ((DoctorDetails) item).getName() + " " + ((DoctorDetails) item).getLastName() + " :" + ((DoctorDetails) item).getHospitalName()+ " ," +((DoctorDetails) item).getHospitalAddress();
            } else if(item instanceof Patients) {
                value = ((Patients) item).getName() + " " + ((Patients) item).getLastName();
            } else if(item instanceof ExamDetails) {
                value = "data: " + ((ExamDetails) item).getExaminationData() + "\ntype: " + ((ExamDetails) item).getExaminationType() + ", results: " + ((ExamDetails) item).getExaminationResults() + ", normal: " + ((ExamDetails) item).getExaminationNormalValues() + " " + ((ExamDetails) item).getExaminationUnitOfMeasurement() + ", doctor: "+ ((ExamDetails) item).getDoctorLastName();
            }
            if (value != null && value.isEmpty()) {
                value = "Not found anything";
            }
            Label label = new Label(value);
            textBox.getChildren().add(label);
        }
        close();
    }
    private void close() {
        stage.close();
    }

}
