# Use Maven with Eclipse Temurin JDK base image
FROM maven:3.9.6-eclipse-temurin-11

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Create output directories
RUN mkdir -p output/ExtentReports/SparkReport \
    output/ExtentReports/PdfReport \
    output/ExtentReports/JsonReport \
    output/screenshot

# Install required libraries
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    gnupg2 \
    curl \
    fonts-liberation \
    libappindicator3-1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libcups2 \
    libnspr4 \
    libnss3 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxrandr2 \
    libxtst6 \
    lsb-release \
    xdg-utils \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Install Google Chrome via official repo
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Install Chromedriver (matching Chrome version from Chrome-for-Testing API)
RUN CHROME_VERSION=$(/usr/bin/google-chrome-stable --version | grep -oE '[0-9]+\.[0-9]+\.[0-9]+') && \
    echo "Detected Chrome version: $CHROME_VERSION" && \
    CHROME_MAJOR=$(echo $CHROME_VERSION | cut -d. -f1) && \
    DRIVER_VERSION=$(curl -s https://googlechromelabs.github.io/chrome-for-testing/LATEST_RELEASE_${CHROME_MAJOR}) && \
    echo "Using Chromedriver version: $DRIVER_VERSION" && \
    wget -q -O /tmp/chromedriver.zip "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${DRIVER_VERSION}/linux64/chromedriver-linux64.zip" && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin && \
    mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    rm -rf /tmp/chromedriver.zip /usr/local/bin/chromedriver-linux64

# Set environment variables for Chrome and Chromedriver paths
ENV CHROME_BIN="/usr/bin/google-chrome-stable"
ENV CHROMEDRIVER_BIN="/usr/local/bin/chromedriver"

# Verify installations
RUN $CHROME_BIN --version && $CHROMEDRIVER_BIN --version

# Command to run Maven tests on container start
CMD ["mvn", "clean", "test", \
     "-Dsurefire.argLine=-Dfile.encoding=UTF-8 -Dmaven.compiler.parameters=true", \
     "-DtestFailureIgnore=false"]
