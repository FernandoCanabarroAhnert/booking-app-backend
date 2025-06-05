package com.fernandocanabarro.booking_app_backend.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminUpdateBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDashboardSummaryDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.excel.BookingsExcelExporter;
import com.fernandocanabarro.booking_app_backend.services.jasper.JasperService;
import com.fernandocanabarro.booking_app_backend.utils.DateUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;
    private final JasperService jasperService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> findAll(Pageable pageable,
                                                        @RequestParam(required = false) List<String> paymentType,
                                                        @RequestParam(required = false) String checkIn,
                                                        @RequestParam(required = false) String checkOut,
                                                        @RequestParam(required = false) Long hotelId,
                                                        @RequestParam(required = false) BigDecimal minPrice,
                                                        @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(this.bookingService.findAllPageable(pageable, 
            DateUtils.convertStringParamToLocalDate(checkIn), DateUtils.convertStringParamToLocalDate(checkOut), hotelId, minPrice, maxPrice, paymentType));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<BookingDetailResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.bookingService.findById(id, true));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> adminCreateBooking(@Valid @RequestBody AdminBookingRequestDTO request) {
        this.bookingService.createBooking(request, false);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> adminUpdateBooking(@PathVariable Long id, @Valid @RequestBody AdminUpdateBookingRequestDTO request) {
        this.bookingService.updateBooking(id, request, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> adminUpdateBookingPayment(@PathVariable Long id, @Valid @RequestBody BookingPaymentRequestDTO request) {
        this.bookingService.updateBookingPayment(id, request, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/self")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfCreateBooking(@Valid @RequestBody BookingRequestDTO request) {
        this.bookingService.createBooking(request, true);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}/self")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdateBooking(@PathVariable Long id, @Valid @RequestBody BaseBookingRequestDTO request) {
        this.bookingService.updateBooking(id, request, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/payment/self")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdateBookingPayment(@PathVariable Long id, @Valid @RequestBody BookingPaymentRequestDTO request) {
        this.bookingService.updateBookingPayment(id, request, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingDetailResponseDTO>> findMyBookings(Pageable pageable) {
        return ResponseEntity.ok(this.bookingService.findAllBookingsByUser(null, pageable, true));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<BookingDashboardSummaryDTO> getDashboardSummary(@RequestParam(required = false) Long hotelId) {
        return ResponseEntity.ok(this.bookingService.getDashboardSummary(hotelId));
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToPdf(HttpServletResponse response,
                        @RequestParam(required = false) BigDecimal minAmount,
                        @RequestParam(required = false) BigDecimal maxAmount,
                        @RequestParam(required = false) Long hotelId,
                        @RequestParam(required = false) Long roomId,
                        @RequestParam(required = false) Long userId,
                        @RequestParam(required = false) String minCheckInDate,
                        @RequestParam(required = false) String maxCheckOutDate,
                        @RequestParam(required = false) String dinheiro,
                        @RequestParam(required = false) String cartao,
                        @RequestParam(required = false) String pix,
                        @RequestParam(required = false) String boleto){
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "bookings_" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.addParams("MIN_AMOUNT", minAmount);
        jasperService.addParams("MAX_AMOUNT", maxAmount);
        jasperService.addParams("HOTEL_ID", hotelId);
        jasperService.addParams("ROOM_ID", roomId);
        jasperService.addParams("USER_ID", userId);
        jasperService.addParams("MIN_CHECK_IN_DATE", DateUtils.convertStringParamToDate(minCheckInDate));
        jasperService.addParams("MAX_CHECK_OUT_DATE", DateUtils.convertStringParamToDate(maxCheckOutDate));
        jasperService.addParams("DINHEIRO_PAYMENT", dinheiro == null ? null : dinheiro.isBlank() ? null : dinheiro);
        jasperService.addParams("CARTAO_PAYMENT", cartao == null ? null : cartao.isBlank() ? null : cartao);
        jasperService.addParams("PIX_PAYMENT", pix == null ? null : pix.isBlank() ? null : pix);
        jasperService.addParams("BOLETO_PAYMENT", boleto == null ? null : boleto.isBlank() ? null : boleto);
        jasperService.exportToPdf(response, JasperService.BOOKINGS);
    }

    @GetMapping("/{id}/pdf")
    public void exportBookingSummaryToPdf(HttpServletResponse response, @PathVariable Long id) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "booking_" + id + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.addParams("BOOKING_ID", id);
        jasperService.exportToPdf(response, JasperService.BOOKING_SUMMARY);
    }

    @GetMapping("/{id}/boleto/pdf")
    public void exportBoletoToPdfFromEmail(HttpServletResponse response, @PathVariable Long id) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "boleto_" + id + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        BookingDetailResponseDTO booking = this.bookingService.findById(id, false);
        jasperService.addParams("USER_ID", booking.getUser().getId());
        jasperService.addParams("AMOUNT", booking.getTotalPrice());
        jasperService.exportToPdf(response, JasperService.BOLETO);
    }

    @GetMapping("/boleto/pdf")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public void exportBoletoToPdfFromAmount(HttpServletResponse response, @RequestParam BigDecimal amount) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "boleto" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.addParams("USER_ID", authService.getMe().getId());
        jasperService.addParams("AMOUNT", amount);
        jasperService.exportToPdf(response, JasperService.BOLETO);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "bookings_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<BookingDetailResponseDTO> bookings = this.bookingService.findAllBookingsDetailed();
        BookingsExcelExporter bookingsExcelExporter = new BookingsExcelExporter(bookings);
        bookingsExcelExporter.export(response);
    }
}
