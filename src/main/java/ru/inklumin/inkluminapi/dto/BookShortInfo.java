package ru.inklumin.inkluminapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookShortInfo {
  private String uuid;
  private String bookTitle;
}
