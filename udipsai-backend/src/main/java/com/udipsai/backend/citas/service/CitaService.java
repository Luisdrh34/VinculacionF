package com.udipsai.backend.citas.service;

import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.citas.persistence.repository.VistaCitasCompletaRepository;
import com.udipsai.backend.citas.persistence.entity.VistaCitasCompleta;
import com.udipsai.backend.usuarios.service.AreaService;
import com.udipsai.backend.usuarios.service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.citas.persistence.entity.PacienteEntity;
import com.udipsai.backend.citas.persistence.repository.PacienteRepository;
import com.udipsai.backend.citas.persistence.entity.CitaEntity;
import com.udipsai.backend.citas.persistence.repository.CitaRepository;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import com.udipsai.backend.citas.service.dto.CitaDTO;
import com.udipsai.backend.citas.service.dto.PacienteDTO;
import com.udipsai.backend.usuarios.service.dto.ProfesionalDTO;
import com.udipsai.backend.citas.service.dto.RegistrarCitaDTO;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepo;

    @Autowired
    private PacienteRepository pacienteRepo;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private VistaCitasCompletaRepository vistaCitasCompletaRepository;

    private static final Logger logger = LoggerFactory.getLogger(CitaService.class);

    // Mapear de una Cita a DTO.
    public CitaDTO mapearDTO(CitaEntity cita, PacienteDTO paciente, ProfesionalDTO profesional, AreaDTO area) {
        CitaDTO dto = new CitaDTO(
                cita.getIdCita(),
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin(),
                cita.getEstado().toString(),
                paciente,
                profesional,
                area);
        return dto;
    }

    // Obtener todas las Citas.
    public ResponseEntity<Page<CitaEntity>> obtenerCitas(Pageable pageable, HttpServletRequest request) {
        logger.info("obtenerCitas()");
        logger.info("Obteniendo todas las Citas");
        Page<CitaEntity> citas = citaRepo.findAll(pageable);

        if (citas.isEmpty()) {
            throw new ResourceNotFoundException("No existen citas registradas");
        }

        logger.info("200 OK: Citas obtenidas correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener una Cita por Id.
    public ResponseEntity<?> obtenerCitaPorId(Long idCita, HttpServletRequest request) {
        logger.info("obtenerCitaPorId()");
        logger.info("Obteniendo cita con id {}", idCita);

        CitaEntity cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + idCita + " no encontrada"));

        PacienteEntity pacienteEncontrado = pacienteRepo.findById(cita.getFichaPaciente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente con ficha " + cita.getFichaPaciente() + " asignado a la cita no fue encontrado"));

        PacienteDTO paciente = pacienteService.mapearDTO(pacienteEncontrado);
        ProfesionalDTO profesional = profesionalService.obtenerProfesionalPorId(cita.getProfesionalId()).getBody();
        AreaDTO area = areaService.obtenerAreaPorId(cita.getArea().getIdArea()).getBody();

        if (profesional == null) {
            throw new ResourceNotFoundException(
                    "Profesional con id " + cita.getProfesionalId() + " asignado a la cita no fue encontrado");
        }

        if (area == null) {
            throw new ResourceNotFoundException(
                    "Area con id " + cita.getArea().getIdArea() + " asignada a la cita no fue encontrada");
        }

        CitaDTO dto = mapearDTO(cita, paciente, profesional, area);
        logger.info("200 OK: Cita obtenida correctamente");

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Registrar una Cita.
    public ResponseEntity<?> registrarCita(RegistrarCitaDTO dto, HttpServletRequest request) {
        logger.info("registrarCita()");
        logger.info("Registrando una Cita");

        if (dto.getFichaPaciente() == null || dto.getProfesionalId() == null || dto.getAreaId() == null
                || dto.getFecha() == null || dto.getHora() == null) {
            throw new InvalidRequestBodyException("Faltan datos para el registro de la cita");
        }

        PacienteEntity pacienteEncontrado = pacienteRepo.findById(dto.getFichaPaciente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente con ficha " + dto.getFichaPaciente() + " no encontrado para el registro de la cita"));

        PacienteDTO paciente = pacienteService.mapearDTO(pacienteEncontrado);

        ProfesionalDTO profesional = profesionalService.obtenerProfesionalPorId(dto.getProfesionalId()).getBody();

        AreaDTO area = areaService.obtenerAreaPorId(dto.getAreaId()).getBody();

        if (profesional == null) {
            throw new ResourceNotFoundException(
                    "Profesional con id " + dto.getProfesionalId() + " no encontrado para el registro de la cita");
        }

        if (area == null) {
            throw new ResourceNotFoundException(
                    "Area con id " + dto.getAreaId() + " no encontrada para el registro de la cita");
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndFichaPaciente(CitaEntity.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getFichaPaciente())) {
            throw new DataConflictException(
                    "Paciente " + paciente.getNombresApellidos().trim()
                            + " ya tiene una cita asignada en la fecha "
                            + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndProfesionalId(CitaEntity.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getProfesionalId())) {
            throw new DataConflictException("Profesional " + profesional.getNombres().trim().toUpperCase() + " "
                    + profesional.getApellidos().trim().toUpperCase() + " ya tiene una cita asignada en la fecha "
                    + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        CitaEntity cita = new CitaEntity();
        cita.setFecha(dto.getFecha());
        cita.setHoraInicio(dto.getHora());
        cita.setHoraFin(dto.getHora().plusMinutes(60));
        cita.setEstado(CitaEntity.Estado.PENDIENTE);
        cita.setFichaPaciente(dto.getFichaPaciente());
        cita.setProfesionalId(dto.getProfesionalId());
        // Inicializa el Ã¡rea si es necesario
        if (cita.getArea() == null) {
            AreaEntity areaT = new AreaEntity();
            areaT.setIdArea(dto.getAreaId());
            cita.setArea(areaT); // Asigna el Ã¡rea inicializada a la cita
        } else {
            cita.getArea().setIdArea(dto.getAreaId());
        }

        CitaEntity citaGuardada = citaRepo.save(cita);
        CitaDTO citaDto = mapearDTO(citaGuardada, paciente, profesional, area);
        logger.info("201 Created: Cita registrada correctamente");

        return new ResponseEntity<>(citaDto, HttpStatus.CREATED);
    }

    // Reagendar una Cita.
    public ResponseEntity<?> reagendarCita(Long idCita, RegistrarCitaDTO dto, HttpServletRequest request) {
        logger.info("reagendarCita()");
        logger.info("Reagendando una Cita");

        CitaEntity citaEncontrada = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (citaEncontrada.getEstado() != CitaEntity.Estado.FALTA_JUSTIFICADA
                && citaEncontrada.getEstado() != CitaEntity.Estado.FALTA_INJUSTIFICADA) {
            throw new DataConflictException(
                    "La cita no se puede reagendar porque se encuentra en estado pendiente, ha finalizado o fue cancelada");
        }

        if (dto.getFichaPaciente() == null || dto.getProfesionalId() == null || dto.getAreaId() == null
                || dto.getFecha() == null || dto.getHora() == null) {
            throw new InvalidRequestBodyException("Faltan datos para el reagendamiento de la cita");
        }

        PacienteEntity pacienteEncontrado = pacienteRepo.findById(dto.getFichaPaciente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente con ficha " + dto.getFichaPaciente()
                                + " no encontrado para el reagendamiento de la cita"));
        PacienteDTO paciente = pacienteService.mapearDTO(pacienteEncontrado);

        ProfesionalDTO profesional = profesionalService.obtenerProfesionalPorId(dto.getProfesionalId()).getBody();

        AreaDTO area = areaService.obtenerAreaPorId(dto.getAreaId()).getBody();

        if (profesional == null) {
            throw new ResourceNotFoundException(
                    "Profesional con id " + dto.getProfesionalId()
                            + " no encontrado para el reagendamiento de la cita");
        }

        if (area == null) {
            throw new ResourceNotFoundException(
                    "Area con id " + dto.getAreaId() + " no encontrada para el reagendamiento de la cita");
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndFichaPaciente(CitaEntity.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getFichaPaciente())) {
            throw new DataConflictException(
                    "Paciente " + paciente.getNombresApellidos().trim()
                            + " ya tiene una cita asignada en la fecha "
                            + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndProfesionalId(CitaEntity.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getProfesionalId())) {
            throw new DataConflictException("Profesional " + profesional.getNombres().trim().toUpperCase() + " "
                    + profesional.getApellidos().trim().toUpperCase() + " ya tiene una cita asignada en la fecha "
                    + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        citaEncontrada.setFecha(dto.getFecha());
        citaEncontrada.setHoraInicio(dto.getHora());
        citaEncontrada.setHoraFin(dto.getHora().plusMinutes(60));
        citaEncontrada.setEstado(CitaEntity.Estado.PENDIENTE);

        CitaEntity citaGuardada = citaRepo.save(citaEncontrada);
        CitaDTO citaDto = mapearDTO(citaGuardada, paciente, profesional, area);
        logger.info("200 OK: Cita reagendada correctamente");

        return new ResponseEntity<>(citaDto, HttpStatus.OK);
    }

    // Obtener todas las Citas por estado.
    public ResponseEntity<Page<CitaDTO>> obtenerCitasPorEstado(CitaEntity.Estado estado, Pageable pageable,
            HttpServletRequest request) {
        logger.info("obtenerCitasPorEstado()");
        logger.info("Obteniendo todas las Citas con estado {}", estado.toString());

        Page<CitaEntity> citas = citaRepo.findAllByEstado(estado, pageable);

        if (citas.isEmpty()) {
            throw new ResourceNotFoundException("No existen citas con estado " + estado.toString());
        }

        Page<CitaDTO> dtos = citas.map(cita -> {
            Optional<PacienteEntity> pacienteOpt = pacienteRepo.findById(cita.getFichaPaciente());
            if (pacienteOpt.isPresent()) {
                PacienteDTO paciente = pacienteService.mapearDTO(pacienteOpt.get());
                ProfesionalDTO profesional = profesionalService.obtenerProfesionalPorId(cita.getProfesionalId()).getBody();
                AreaDTO area = areaService.obtenerAreaPorId(cita.getArea().getIdArea()).getBody();
                return mapearDTO(cita, paciente, profesional, area);
            } else {
                throw new ResourceNotFoundException("Paciente no encontrado");
            }
        });
        logger.info("200 OK: Citas con estado {} obtenidas correctamente", estado.toString());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // Obtener Citas por filtros.
    public ResponseEntity<Page<CitaEntity>> obtenerCitasPorFiltros(Long idCita, Long fichaPaciente, LocalDate fecha,
            Pageable pageable,
            HttpServletRequest request) {
        logger.info("obtenerCitasPorFiltro()");
        logger.info("Obteniendo citas por filtro");

        Page<CitaEntity> citas = Page.empty();

        if (fecha != null && idCita == null && fichaPaciente == null) {
            citas = citaRepo.findAllByFecha(fecha, pageable);
        } else {
            citas = citaRepo.findCitasByFilter(idCita, fichaPaciente, pageable);
        }

        if (citas.isEmpty()) {
            // Devolver una pÃ¡gina vacÃ­a si no hay citas
            return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
        }

        logger.info("200 OK: Citas obtenidas por filtro correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Encontrar horas libres de un Profesional en una fecha especifica.
    public ResponseEntity<List<String>> encontrarHorasLibresProfesional(Long profesionalId, LocalDate fecha,
            HttpServletRequest request) {
        logger.info("encontrarHorasLibresProfesional()");
        logger.info("Encontrando horas libres de Profesional con id {} en una fecha especifica {}", profesionalId,
                fecha.toString());
        ProfesionalDTO profesional = profesionalService.obtenerProfesionalPorId(profesionalId).getBody();

        if (profesional == null) {
            throw new ResourceNotFoundException("Profesional con id " + profesionalId + " no encontrado");
        }

        List<LocalTime> horasOcupadas = citaRepo.findHorasOcupadasByProfesionalAndFecha(profesionalId, fecha);
        List<String> horasLibres = new ArrayList<>();

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaReceso = LocalTime.of(12, 0);
        LocalTime horaFin = LocalTime.of(17, 0);

        while (horaInicio.isBefore(horaFin)) {
            if (!horasOcupadas.contains(horaInicio) && !horaInicio.equals(horaReceso)) {
                horasLibres.add(horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            // Asumiendo citas de 1 hora de duraciÃ³n. Ajusta segÃºn sea necesario.
            horaInicio = horaInicio.plusHours(1);
        }
        logger.info("200 OK: Horas libres encontradas correctamente");
        return new ResponseEntity<>(horasLibres, HttpStatus.OK);
    }

    // Finalizar una Cita.
    public ResponseEntity<?> finalizarCita(Long idCita) {
        logger.info("finalizarCita()");
        logger.info("Finalizando una Cita");

        CitaEntity cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + idCita + " no encontrada"));

        if (cita.getEstado() != CitaEntity.Estado.PENDIENTE) {
            throw new DataConflictException(
                    "La cita no se puede finalizar porque anteriormente ya ha finalizado o ya fue cancelada");
        }

        cita.setEstado(CitaEntity.Estado.FINALIZADA);
        citaRepo.save(cita);
        logger.info("200 OK: Cita finalizada correctamente");
        CitaResponse citaResponse = new CitaResponse("Cita finalizada correctamente");
        return ResponseEntity.ok().body(citaResponse);

    }

    // Cancelar una Cita.
    public ResponseEntity<?> cancelarCita(Long idCita) {
        logger.info("cancelarCita()");
        logger.info("Cancelando una Cita");

        CitaEntity cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + idCita + " no encontrada"));

        if (cita.getEstado() != CitaEntity.Estado.PENDIENTE) {
            throw new DataConflictException("La cita no se puede cancelar porque ya ha finalizado o ya fue cancelada");
        }

        cita.setEstado(CitaEntity.Estado.CANCELADA);
        citaRepo.save(cita);
        CitaResponse response = new CitaResponse("Cita cancelada correctamente");

        return ResponseEntity.ok().body(response);
    }

    // Cambiar estado de cita a Falta Justificada.
    public ResponseEntity<?> faltaJustificada(Long idCita) {
        logger.info("faltaJustificada()");
        logger.info("Cambiando estado de cita a Falta Justificada");

        CitaEntity cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + idCita + " no encontrada"));

        if (cita.getEstado() != CitaEntity.Estado.PENDIENTE && cita.getEstado() != CitaEntity.Estado.FALTA_INJUSTIFICADA
                && cita.getEstado() != CitaEntity.Estado.FALTA_JUSTIFICADA) {
            throw new DataConflictException(
                    "La cita no se puede asignar como FALTA JUSTIFICADA porque ya ha finalizado o ya fue cancelada");
        }

        cita.setEstado(CitaEntity.Estado.FALTA_JUSTIFICADA);
        citaRepo.save(cita);
        CitaResponse response = new CitaResponse("Cita asignada como FALTA JUSTIFICADA correctamente");

        return ResponseEntity.ok().body(response);
    }

    // Cambiar estado de cita a Falta Injustificada.
    public ResponseEntity<?> faltaInjustificada(Long idCita) {
        logger.info("faltaInjustificada()");
        logger.info("Cambiando estado de cita a Falta Injustificada");

        CitaEntity cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + idCita + " no encontrada"));

        if (cita.getEstado() != CitaEntity.Estado.PENDIENTE && cita.getEstado() != CitaEntity.Estado.FALTA_INJUSTIFICADA
                && cita.getEstado() != CitaEntity.Estado.FALTA_JUSTIFICADA) {
            throw new DataConflictException(
                    "La cita no se puede asignar como FALTA INJUSTIFICADA porque ya ha finalizado o ya fue cancelada");
        }

        cita.setEstado(CitaEntity.Estado.FALTA_INJUSTIFICADA);
        citaRepo.save(cita);
        CitaResponse response = new CitaResponse("Cita asignada como FALTA INJUSTIFICADA correctamente");

        return ResponseEntity.ok().body(response);
    }

    static class CitaResponse {
        private String message;

        public CitaResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // obtener citas filtro unico

    public ResponseEntity<Page<CitaEntity>> obtenerCitasFiltro(String filtro, Pageable pageable) {
        logger.info("obtenerCitasFiltro()");
        logger.info("Obteniendo citas por filtro");

        Page<CitaEntity> citas = citaRepo.findCitasFiltro(filtro, pageable);

        logger.info("200 OK: Citas obtenidas por filtro correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener citas por Profesional
    public ResponseEntity<Page<CitaEntity>> obtenerCitasPorProfesional(Long idProfesional, Pageable pageable) {
        logger.info("obtenerCitasPorProfesional()");
        logger.info("Obteniendo citas por Profesional");

        Page<CitaEntity> citas = citaRepo.findAllByProfesionalId(idProfesional, pageable);

        logger.info("200 OK: Citas obtenidas por Profesional correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener citas por area
    public ResponseEntity<Page<CitaEntity>> obtenerCitasPorArea(Long idArea, Pageable pageable) {
        logger.info("obtenerCitasPorArea()");
        logger.info("Obteniendo citas por Area");

        Page<CitaEntity> citas = citaRepo.findAllByArea_IdArea(idArea, pageable);

        logger.info("200 OK: Citas obtenidas por Area correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener citas por una lista de areas
    public ResponseEntity<Page<CitaEntity>> obtenerCitasPorAreas(List<Long> areas, Pageable pageable) {
        logger.info("obtenerCitasPorAreas()");
        logger.info("Obteniendo citas por Ã¡reas");

        // Lista para almacenar todas las citas encontradas
        List<CitaEntity> allCitas = new ArrayList<>();

        // Obtener las citas por cada Ã¡rea y agregarlas a la lista
        for (Long area : areas) {
            Page<CitaEntity> citasArea = citaRepo.findAllByArea_IdArea(area, pageable);
            allCitas.addAll(citasArea.getContent()); // Agregar el contenido de la pÃ¡gina al total
        }

        // Verificar si no se encontraron citas
        if (allCitas.isEmpty()) {
            logger.info("No se encontraron citas para las Ã¡reas proporcionadas");
            return new ResponseEntity<>(Page.empty(pageable), HttpStatus.OK);
        }

        // Calcular el rango de las citas a mostrar en la pÃ¡gina actual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCitas.size());

        // Crear una lista paginada basada en el rango calculado
        List<CitaEntity> paginatedList = allCitas.subList(start, end);

        // Crear un objeto Page con la lista paginada y la informaciÃ³n del pageable
        // original
        Page<CitaEntity> pageCitas = new PageImpl<>(paginatedList, pageable, allCitas.size());

        logger.info("200 OK: Citas obtenidas por Ã¡reas correctamente");
        return new ResponseEntity<>(pageCitas, HttpStatus.OK);
    }

    // Obtener citas por paciente
    public ResponseEntity<Page<CitaEntity>> obtenerCitasPorPaciente(Long idPaciente, Pageable pageable) {
        logger.info("obtenerCitasPorPaciente()");
        logger.info("Obteniendo citas por Paciente");

        Page<CitaEntity> citas = citaRepo.findAllByFichaPaciente(idPaciente, pageable);

        logger.info("200 OK: Citas obtenidas por Paciente correctamente");

        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // Obtener citas por paciente Completa
    public ResponseEntity<Page<VistaCitasCompleta>> obtenerCitasCompletasPorPaciente(int idPaciente,
            Pageable pageable) {
        Page<VistaCitasCompleta> citas = vistaCitasCompletaRepository.findByFichaPaciente(idPaciente, pageable);
        return new ResponseEntity<>(citas, HttpStatus.OK);

    }
}


