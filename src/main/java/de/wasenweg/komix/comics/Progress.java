package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Progress {

    @Builder.Default
    private boolean read = false;

    @NonNull
    @Builder.Default
    private Short currentPage = 0;

    private Date lastRead;
}
