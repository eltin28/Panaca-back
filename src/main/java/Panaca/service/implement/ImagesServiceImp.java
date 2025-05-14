package Panaca.service.implement;

import Panaca.exceptions.FileNotFoundException;
import Panaca.service.service.ImagesService;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@Validated

public class ImagesServiceImp implements ImagesService {

    @Override
    public String subirImagen(MultipartFile multipartFile) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());

        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                blob.getName());
    }

    @Override
    public void eliminarImagen(String nombreImagen) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(nombreImagen);
        if (blob == null || !blob.exists()) {
            throw new FileNotFoundException("No se encontró la imagen: " + nombreImagen);
        }
        blob.delete();
    }
}

