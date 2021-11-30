package com.back.route4d.controller;

import com.back.route4d.helper.Helper;
import com.back.route4d.model.FileInfo;
import com.back.route4d.message.ResponseMessage;
import com.back.route4d.model.Pedido;
import com.back.route4d.repository.PedidoRepository;
import com.back.route4d.services.FilesStorageService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.back.route4d.services.impl.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/archivos")
public class FilesController {

    @Autowired
    FilesStorageService storageService;

    @Autowired
    PedidoRepository pedidoRepository;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });

            try {
                // TODO: terminar lectura con más de un archivo
                // TODO: validar nombre del archivo
                // Por ahora, el proceso está limitado a agregar 100 pedidos por día, por los 5 primeros días
                String fileName = FileStorageServiceImpl.folderName + "/" + fileNames.get(0);
                final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                String strYearMonth = Helper.getOrdersDateFromName(fileName); // datos del nombre del archivo
                String line; // línea del archivo
                int id = 1; // contador para identificador
                List<Pedido> listaPedidos = new ArrayList<>(); // para almacenar pedidos
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

                // Leyendo datos del archivo
                int dayAnt = 0;     // eliminar luego
                int pedidoCont = 0; // eliminar luego
                while ((line = br.readLine()) != null) {
                    final String[] tokens = line.trim().split(",");
                    final String[] date = tokens[0].trim().split(":");
                    final int day = Integer.parseInt(date[0]);

                    if (dayAnt != day) { // eliminar luego
                        pedidoCont = 0;
                    }

                    if (day >= 1 && day <= 5 && pedidoCont < 100) { // eliminar luego últ. condición
                        dayAnt = day; // eliminar luego

                        final int hour = Integer.parseInt(date[1]);
                        final int min = Integer.parseInt(date[2]);
                        final int x = Integer.parseInt(tokens[1]);
                        final int y = Integer.parseInt(tokens[2]);
                        final int demand = Integer.parseInt(tokens[3]);
                        final int remaining = Integer.parseInt(tokens[4]);
                        String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
                        LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);
                        Pedido pedido = new Pedido(id++, x, y, demand, remaining, orderDate,0);
                        listaPedidos.add(pedido);
                        pedidoCont++; // eliminar luego
                    }
                    else if (day > 5) {
                        break;
                    }
                }

                br.close();

                pedidoRepository.saveAll(listaPedidos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            message = "Archivo cargado correctamente: " + fileNames;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Error al cargar los archivos!";
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
