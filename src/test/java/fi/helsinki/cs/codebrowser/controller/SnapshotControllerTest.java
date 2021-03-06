package fi.helsinki.cs.codebrowser.controller;

import fi.helsinki.cs.codebrowser.app.App;
import fi.helsinki.cs.codebrowser.exception.NotFoundException;
import fi.helsinki.cs.codebrowser.model.Snapshot;
import fi.helsinki.cs.codebrowser.service.SnapshotService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;

import static org.hamcrest.CoreMatchers.is;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@ActiveProfiles("test")
public final class SnapshotControllerTest {

    private static final String SNAPSHOT_A_ID = "snapshotA";
    private static final String SNAPSHOT_B_ID = "snapshotB";

    private static final String INSTANCE = "instance";
    private static final String STUDENT = "studentID";
    private static final String COURSE = "courseID";
    private static final String EXERCISE = "exerciseID";
    private static final String SNAPSHOT = "snapshotID";
    private static final String LEVEL = "KEY";

    private static final String BASE_URL_A = "/" + INSTANCE + "/students/" + STUDENT + "/courses/" + COURSE + "/exercises/" + EXERCISE + "/snapshots";
    private static final String BASE_URL_B = "/" + INSTANCE + "/courses/" + COURSE + "/exercises/" + EXERCISE + "/students/" + STUDENT + "/snapshots";

    @Mock
    private SnapshotService snapshotService;

    @InjectMocks
    private SnapshotController snapshotController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(snapshotController).build();
    }

    private List<Snapshot> buildSnapshotList() {

        final List<Snapshot> snapshots = new ArrayList<>();

        final Snapshot snapshotA = new Snapshot();
        snapshotA.setId(SNAPSHOT_A_ID);
        snapshots.add(snapshotA);

        final Snapshot snapshotB = new Snapshot();
        snapshotB.setId(SNAPSHOT_B_ID);
        snapshots.add(snapshotB);

        return snapshots;
    }

    @Test
    public void listReturnsAllSnapshotsForUrlStartingWithStudent() throws Exception {

        final List<Snapshot> snapshots = buildSnapshotList();

        when(snapshotService.findAllBy(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL)).thenReturn(snapshots);

        mockMvc.perform(get(BASE_URL_A))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].id", is(SNAPSHOT_A_ID)))
               .andExpect(jsonPath("$[1].id", is(SNAPSHOT_B_ID)));

        verify(snapshotService, times(1)).findAllBy(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL);
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    public void listReturnsAllSnapshotsForUrlStartingWithCourse() throws Exception {

        final List<Snapshot> snapshots = buildSnapshotList();

        when(snapshotService.findAllBy(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL)).thenReturn(snapshots);

        mockMvc.perform(get(BASE_URL_B))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].id", is(SNAPSHOT_A_ID)))
               .andExpect(jsonPath("$[1].id", is(SNAPSHOT_B_ID)));

        verify(snapshotService, times(1)).findAllBy(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL);
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    public void readReturnsSnapshotForUrlStartingWithStudent() throws Exception {

        final Snapshot snapshot = new Snapshot();
        snapshot.setId(SNAPSHOT);

        when(snapshotService.findBy(INSTANCE, STUDENT, COURSE, EXERCISE, SNAPSHOT, LEVEL)).thenReturn(snapshot);

        mockMvc.perform(get(BASE_URL_A + "/" + SNAPSHOT))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(SNAPSHOT)));

        verify(snapshotService, times(1)).findBy(INSTANCE, STUDENT, COURSE, EXERCISE, SNAPSHOT, LEVEL);
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    public void readReturnsSnapshotForUrlStartingWithCourse() throws Exception {

        final Snapshot snapshot = new Snapshot();
        snapshot.setId(SNAPSHOT);

        when(snapshotService.findBy(INSTANCE, STUDENT, COURSE, EXERCISE, SNAPSHOT, LEVEL)).thenReturn(snapshot);

        mockMvc.perform(get(BASE_URL_B + "/" + SNAPSHOT))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(SNAPSHOT)));

        verify(snapshotService, times(1)).findBy(INSTANCE, STUDENT, COURSE, EXERCISE, SNAPSHOT, LEVEL);
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    public void readFilesReturnsZip() throws Exception {

        final byte[] bytes = { 0x00, 0x01, 0x02 };
        when(snapshotService.findFilesAsZip(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL, "", 0)).thenReturn(bytes);

        mockMvc.perform(get(BASE_URL_A + "/files.zip?level=KEY"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/zip"))
               .andExpect(content().bytes(bytes));

        verify(snapshotService).findFilesAsZip(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL, "", 0);
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    public void readFilesPassesLevelToService() throws Exception {

        mockMvc.perform(get(BASE_URL_A + "/files.zip?level=CODE"));

        verify(snapshotService).findFilesAsZip(INSTANCE, STUDENT, COURSE, EXERCISE, "CODE", "", 0);
    }

    @Test
    public void readFilesHandlesNotFoundException() throws Exception {

        when(snapshotService.findFilesAsZip(INSTANCE, STUDENT, COURSE, EXERCISE, LEVEL, "", 0)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL_A + "/files.zip?level=KEY"))
               .andExpect(status().isNotFound());
    }
}
