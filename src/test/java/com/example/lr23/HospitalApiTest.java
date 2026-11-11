package com.example.lr23;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Перевіряє вимоги варіанта «Лікарня» й загальні вимоги до REST-сервісу. */
@SpringBootTest
@AutoConfigureMockMvc
class HospitalApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    // ---------- допоміжні методи ----------

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder request, String role, String body) {
        if (role != null) {
            request.header("X-Role", role);
        }
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return request;
    }

    private long idOf(String responseBody) throws Exception {
        return json.readTree(responseBody).get("id").asLong();
    }

    private long createPatient(String name) throws Exception {
        String body = mvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return idOf(body);
    }

    private long createPrescription(long patientId, String type) throws Exception {
        String body = mvc.perform(as(post("/patients/" + patientId + "/prescriptions"), "DOCTOR",
                        "{\"type\":\"" + type + "\",\"description\":\"тест\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return idOf(body);
    }

    // ---------- пацієнти (CRUD) ----------

    @Test
    void patientCrudWithCorrectStatusCodes() throws Exception {
        long id = createPatient("Іван Петренко");

        mvc.perform(get("/patients/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Іван Петренко"))
                .andExpect(jsonPath("$.discharged").value(false));
        mvc.perform(get("/patients")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int) id)));
        mvc.perform(as(put("/patients/" + id), null, "{\"name\":\"Іван П.\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Іван П."));
        mvc.perform(delete("/patients/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/patients/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void invalidInputIsRejectedWith400() throws Exception {
        mvc.perform(as(post("/patients"), null, "{\"name\":\"  \"}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").exists());
        mvc.perform(as(post("/patients"), null, "{}")).andExpect(status().isBadRequest());
        mvc.perform(as(post("/patients"), null, "{ not json")).andExpect(status().isBadRequest());
        mvc.perform(get("/patients/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void unknownResourcesGive404AndUnsupportedMethodGives405() throws Exception {
        mvc.perform(get("/patients/999999")).andExpect(status().isNotFound());
        mvc.perform(get("/prescriptions/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/prescriptions/1")).andExpect(status().isMethodNotAllowed());
    }

    // ---------- діагноз ----------

    @Test
    void onlyDoctorSetsDiagnosis() throws Exception {
        long id = createPatient("Марія");
        String url = "/patients/" + id + "/diagnosis";

        mvc.perform(as(post(url), "NURSE", "{\"diagnosis\":\"Грип\"}")).andExpect(status().isForbidden());
        mvc.perform(as(post(url), null, "{\"diagnosis\":\"Грип\"}")).andExpect(status().isForbidden());
        mvc.perform(as(post(url), "DOCTOR", "{\"diagnosis\":\"\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(post(url), "DOCTOR", "{\"diagnosis\":\"Грип\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Грип"));
        mvc.perform(as(post("/patients/999999/diagnosis"), "DOCTOR", "{\"diagnosis\":\"Грип\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- призначення ----------

    @Test
    void onlyDoctorCreatesPrescriptionsOfThreeTypes() throws Exception {
        long id = createPatient("Олег");
        String url = "/patients/" + id + "/prescriptions";

        for (String type : new String[] {"PROCEDURE", "MEDICINE", "OPERATION"}) {
            mvc.perform(as(post(url), "DOCTOR", "{\"type\":\"" + type + "\",\"description\":\"d\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.type").value(type))
                    .andExpect(jsonPath("$.done").value(false))
                    .andExpect(jsonPath("$.patientId").value(id));
        }
        mvc.perform(as(post(url), "NURSE", "{\"type\":\"MEDICINE\"}")).andExpect(status().isForbidden());
        mvc.perform(as(post(url), "DOCTOR", "{\"type\":\"MAGIC\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(post(url), "DOCTOR", null)).andExpect(status().isBadRequest());
        mvc.perform(as(post("/patients/999999/prescriptions"), "DOCTOR", "{\"type\":\"MEDICINE\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- виконання призначень ----------

    @Test
    void nurseCannotPerformOperation() throws Exception {
        long operation = createPrescription(createPatient("Анна"), "OPERATION");

        mvc.perform(as(post("/prescriptions/" + operation + "/execute"), "NURSE", null))
                .andExpect(status().isForbidden());
        // призначення лишилось невиконаним
        mvc.perform(get("/prescriptions/" + operation)).andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void nursePerformsProceduresAndMedicine() throws Exception {
        long patient = createPatient("Богдан");
        for (String type : new String[] {"PROCEDURE", "MEDICINE"}) {
            long id = createPrescription(patient, type);
            mvc.perform(as(post("/prescriptions/" + id + "/execute"), "NURSE", null)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.done").value(true))
                    .andExpect(jsonPath("$.doneBy").value("NURSE"));
        }
    }

    @Test
    void doctorPerformsAnyPrescription() throws Exception {
        long patient = createPatient("Софія");
        for (String type : new String[] {"PROCEDURE", "MEDICINE", "OPERATION"}) {
            long id = createPrescription(patient, type);
            mvc.perform(as(post("/prescriptions/" + id + "/execute"), "DOCTOR", null)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.doneBy").value("DOCTOR"));
        }
    }

    @Test
    void prescriptionCanBeExecutedOnlyOnce() throws Exception {
        long id = createPrescription(createPatient("Дарина"), "PROCEDURE");

        mvc.perform(as(post("/prescriptions/" + id + "/execute"), "NURSE", null)).andExpect(status().isOk());
        mvc.perform(as(post("/prescriptions/" + id + "/execute"), "DOCTOR", null)).andExpect(status().isConflict());
    }

    @Test
    void executionRequiresKnownRoleAndExistingPrescription() throws Exception {
        long id = createPrescription(createPatient("Єва"), "PROCEDURE");

        mvc.perform(as(post("/prescriptions/" + id + "/execute"), null, null)).andExpect(status().isForbidden());
        mvc.perform(as(post("/prescriptions/" + id + "/execute"), "JANITOR", null)).andExpect(status().isForbidden());
        mvc.perform(as(post("/prescriptions/999999/execute"), "DOCTOR", null)).andExpect(status().isNotFound());
    }

    // ---------- виписка ----------

    @Test
    void dischargeRecordsFinalDiagnosisAndBlocksRepeat() throws Exception {
        long id = createPatient("Жанна");
        String url = "/patients/" + id + "/discharge";

        mvc.perform(as(post(url), "NURSE", "{\"finalDiagnosis\":\"Одужала\"}")).andExpect(status().isForbidden());
        mvc.perform(as(post(url), "DOCTOR", "{\"finalDiagnosis\":\"\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(post(url), "DOCTOR", "{}")).andExpect(status().isBadRequest());
        mvc.perform(as(post(url), "DOCTOR", null)).andExpect(status().isBadRequest());
        mvc.perform(as(post(url), "DOCTOR", "{\"finalDiagnosis\":\"Одужала\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.discharged").value(true))
                .andExpect(jsonPath("$.finalDiagnosis").value("Одужала"));
        mvc.perform(as(post(url), "DOCTOR", "{\"finalDiagnosis\":\"Інший\"}")).andExpect(status().isConflict());
        mvc.perform(get("/patients/" + id)).andExpect(jsonPath("$.finalDiagnosis").value("Одужала"));
    }

    @Test
    void dischargedPatientCannotGetNewPrescriptions() throws Exception {
        long id = createPatient("Захар");
        mvc.perform(as(post("/patients/" + id + "/discharge"), "DOCTOR", "{\"finalDiagnosis\":\"Здоровий\"}"))
                .andExpect(status().isOk());

        mvc.perform(as(post("/patients/" + id + "/prescriptions"), "DOCTOR", "{\"type\":\"MEDICINE\"}"))
                .andExpect(status().isConflict());
    }

    // ---------- перегляд і фільтр призначень ----------

    @Test
    void prescriptionsCanBeFilteredByStatus() throws Exception {
        long patient = createPatient("Ігор");
        long first = createPrescription(patient, "PROCEDURE");
        createPrescription(patient, "MEDICINE");
        mvc.perform(as(post("/prescriptions/" + first + "/execute"), "NURSE", null)).andExpect(status().isOk());
        String url = "/patients/" + patient + "/prescriptions";

        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get(url).param("status", "done")).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(first));
        mvc.perform(get(url).param("status", "pending")).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].done").value(false));
        mvc.perform(get(url).param("status", "unknown")).andExpect(status().isBadRequest());
        mvc.perform(get("/patients/999999/prescriptions")).andExpect(status().isNotFound());
    }

    @Test
    void prescriptionCanBeDeleted() throws Exception {
        long id = createPrescription(createPatient("Юлія"), "MEDICINE");

        mvc.perform(delete("/prescriptions/" + id)).andExpect(status().isNoContent());
        mvc.perform(delete("/prescriptions/" + id)).andExpect(status().isNotFound());
    }

    // ---------- лікарі та медсестри ----------

    @Test
    void doctorsAndNursesHaveFullCrud() throws Exception {
        for (String resource : new String[] {"doctors", "nurses"}) {
            String created = mvc.perform(as(post("/" + resource), null, "{\"name\":\"Тест\",\"profile\":\"терапевт\"}"))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            long id = idOf(created);

            mvc.perform(get("/" + resource)).andExpect(status().isOk())   // без кінцевого слеша
                    .andExpect(jsonPath("$[*].id", hasItem((int) id)));
            mvc.perform(get("/" + resource + "/" + id)).andExpect(jsonPath("$.profile").value("терапевт"));
            mvc.perform(as(put("/" + resource + "/" + id), null, "{\"name\":\"Новий\",\"profile\":\"хірург\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Новий"));
            mvc.perform(as(post("/" + resource), null, "{\"name\":\"\"}")).andExpect(status().isBadRequest());
            mvc.perform(delete("/" + resource + "/" + id)).andExpect(status().isNoContent());
            mvc.perform(get("/" + resource + "/" + id)).andExpect(status().isNotFound());
            mvc.perform(delete("/" + resource + "/" + id)).andExpect(status().isNotFound());
        }
    }
}
