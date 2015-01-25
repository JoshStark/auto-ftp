package com.github.autoftp.strategies;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.github.autoftp.FileUtilities;
import com.github.autoftp.config.SettingsProvider;

public class MoveOnCompleteStrategyTest {

	@InjectMocks
	private MoveOnCompleteStrategy moveOnCompleteStrategy = new MoveOnCompleteStrategy();
	
	@Mock
	private SettingsProvider mockSettingsProvider;
	
	@Mock
	private FileUtilities mockFileFactory;
	
	@Mock
	private File mockFileToMove;
	
	@Mock
	private File mockDestinationDirectory;
	
	@Before
	public void setUp() {
		initMocks(this);
		
		when(mockSettingsProvider.getDownloadDirectory()).thenReturn("downloaddir");
		when(mockSettingsProvider.getMoveDirectory()).thenReturn("movedir");
		when(mockFileFactory.getFile(Mockito.anyString())).thenReturn(mockFileToMove).thenReturn(mockDestinationDirectory);
	}
	
	@Test
	public void onDownloadCompleteShouldCallSettingsProviderToGetMoveAndDownloadDirectory() {
		
		moveOnCompleteStrategy.onDownloadFinished("file");
		
		verify(mockSettingsProvider).getMoveDirectory();
		verify(mockSettingsProvider).getDownloadDirectory();
	}
	
	@Test
	public void onDownloadCompleteShouldUseFileNameAndDirectoriesToMoveFile() {
		
		moveOnCompleteStrategy.onDownloadFinished("file");
		
		verify(mockFileFactory).getFile("downloaddir/file");
		verify(mockFileFactory).getFile("movedir");
	}
	
	@Test
	public void onDownloadCompleteShouldCallMoveMethodOnFileToMove() throws IOException {
		
		moveOnCompleteStrategy.onDownloadFinished("file");
		
		verify(mockFileFactory).moveFile(mockFileToMove, mockDestinationDirectory);
	}
}
