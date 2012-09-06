package org.getcomposer.core;

public interface ProgressListener {

	void progressChanged(int worked);

	void setTotalWork(int total);

	void worked();

}
