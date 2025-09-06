package tr.mhrs.appointment_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.mhrs.appointment_service.dto.AppointmentResponse;
import tr.mhrs.appointment_service.dto.BookingRequest;
import tr.mhrs.appointment_service.service.AppointmentService;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    
    // Manuel constructor (Lombok çalışmıyorsa)
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(@RequestBody @Valid BookingRequest request) {
        try{
            AppointmentResponse response = appointmentService.bookAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(AppointmentResponse.error(e.getMessage()));
        }
    }


    @DeleteMapping("/{code}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String code, @RequestParam(required = false)String reason) {
        try{
            AppointmentResponse response = appointmentService.cancelAppointment(code, reason);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(AppointmentResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-appointments/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@PathVariable Long patientId) {
        List<AppointmentResponse> appointments = appointmentService.getMyAppointments(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-tc/{tcNo}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByTcNo(@PathVariable String tcNo) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByTc(tcNo);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Appointment Service Çalışıyor");
    }



}
