package com.hark.urlshortener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.model.Paste;

@Mapper(componentModel = "spring")
public interface PasteMapper {

    @Mapping(target = "shortCode", source = "shortCode")
    @Mapping(target = "text", source = "req.text")
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "views", ignore = true)
    Paste toEntity(RequestPaste req, String shortCode);

    @Mapping(target = "shortCode", source = "paste.shortCode")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "newlyCreated", source = "newlyCreated")
    ResponsePaste toResponse(Paste paste, String url, boolean newlyCreated);
}