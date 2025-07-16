# Use a Maven image with Java 11 as the base for building and running tests.
FROM maven:3.9.6-eclipse-temurin-11
 
# Set the working directory inside the container
WORKDIR /app
 
# Copy only pom.xml first to leverage Docker layer caching.
# This allows Docker to cache the dependencies if pom.xml hasn't changed.
COPY pom.xml .
 
# Download dependencies.
# -B for batch mode (non-interactive).
# We run go-offline here to ensure dependencies are available for subsequent steps and final test run.
RUN mvn dependency:go-offline -B
 
# Copy the rest of the project source code
COPY src ./src
 
# Create report directories with appropriate permissions.
# Ensure these paths match what your Java code uses for ExtentReports.
RUN mkdir -p output/ExtentReports/SparkReport \
             output/ExtentReports/PdfReport \
             output/ExtentReports/JsonReport \
             output/screenshots && \
    chmod -R 777 output/ # Grant full permissions for debugging reports. Consider more restrictive in production.
 
# Install Google Chrome and its necessary runtime dependencies.
# Using --no-install-recommends to keep the installation minimal.
# Removed `libdrm-amd-common` and `libxshmfence6` as they were causing "Unable to locate package" errors.
# Add common fonts and utilities required by Chrome for headless operation.
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget \
        curl \
        unzip \
        gnupg2 \
        # Essential libraries for Chrome headless mode
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
        fonts-liberation \
        xdg-utils \
        ca-certificates \
        # Ensure latest certificates are present
        && \
    # Add Google Chrome repository and install stable Chrome
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-linux-signing-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    # Clean up APT caches to reduce image size
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
 
# WebDriverManager in your Java code will handle downloading and setting up ChromeDriver.
# Ensure your Selenium code uses WebDriverManager and sets headless options.
# Example in Java for ChromeOptions (add this to your test setup code):
# ChromeOptions options = new ChromeOptions();
# options.addArguments("--headless=new"); // or just "--headless" for older versions
# options.addArguments("--no-sandbox"); // Required for running Chrome in Docker
# options.addArguments("--disable-dev-shm-usage"); // Overcomes limited /dev/shm in Docker
# options.addArguments("--window-size=1920,1080"); // Set a common window size for screenshots
# WebDriverManager.chromedriver().setup();
# WebDriver driver = new ChromeDriver(options);
 
# Set environment variables for Chrome path. This is good practice.
ENV CHROME_BIN="/usr/bin/google-chrome"
 
# Verify Chrome installation (optional, but good for debugging)
RUN google-chrome --version
 
# Command to run Maven tests when the container starts.
# We no longer need -Dtest=runner.MainApp because Surefire is now configured in pom.xml
CMD ["mvn", "clean", "test", \
     "-Dsurefire.argLine=-Dfile.encoding=UTF-8 -Dmaven.compiler.parameters=true", \
     "-DtestFailureIgnore=false" \
    ]