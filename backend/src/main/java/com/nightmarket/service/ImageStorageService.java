package com.nightmarket.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.nightmarket.exception.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Durable image storage backed by MongoDB GridFS.
 * Ensures uploaded photos survive server restarts and Render dyno spin-downs.
 */
@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final GridFsTemplate gridFsTemplate;

    public ImageStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    /**
     * Validates and stores a multipart image file into MongoDB GridFS.
     *
     * @param file the uploaded image file
     * @return relative URL path to retrieve the image (e.g. "/api/images/652f...")
     * @throws IllegalArgumentException if validation fails
     * @throws IOException if reading the input stream fails
     */
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No image file provided.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image file size exceeds maximum limit of 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            // Also allow general image/* if detected
            if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type: only JPEG, PNG, WEBP, or GIF images are allowed.");
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "listing-image-" + System.currentTimeMillis() + ".jpg";
        }

        try (InputStream inputStream = file.getInputStream()) {
            ObjectId objectId = gridFsTemplate.store(inputStream, originalFilename, contentType);
            log.info("Stored image '{}' into GridFS with ID: {}", originalFilename, objectId.toHexString());
            return "/api/images/" + objectId.toHexString();
        }
    }

    /**
     * Retrieves an image resource by its GridFS ObjectId hex string.
     *
     * @param id the GridFS ObjectId string
     * @return GridFsResource streamable by Spring MVC
     * @throws ResourceNotFoundException if no such image exists
     */
    public GridFsResource getResource(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("Invalid image ID: " + id);
        }

        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
        if (gridFSFile == null) {
            throw new ResourceNotFoundException("Image not found with ID: " + id);
        }

        return gridFsTemplate.getResource(gridFSFile);
    }
}
