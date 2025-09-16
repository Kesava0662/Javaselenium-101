FROM maven:3.9.6-eclipse-temurin-11
# Set working directory
WORKDIR /app
# Copy Maven config first to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy project source
COPY src ./src
# Create report directories
RUN mkdir -p output/ExtentReports/SparkReport \
             output/ExtentReports/PdfReport \
             output/ExtentReports/JsonReport \
             output/screenshots && \
    chmod -R 777 output/
# Install Chrome + GUI tools + VNC/novnc stack
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget curl unzip gnupg2 \
        xvfb x11vnc x11-utils \
        net-tools netcat-openbsd \
        python3 python3-pip \
        supervisor \
        libappindicator3-1 libasound2 libatk-bridge2.0-0 \
        libcups2 libnspr4 libnss3 \
        libxcomposite1 libxdamage1 libxfixes3 libxrandr2 libxtst6 \
        lsb-release fonts-liberation xdg-utils ca-certificates \
&& wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-linux-signing-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    # Install noVNC + websockify
    git clone https://github.com/novnc/noVNC.git /app/novnc && \
    git clone https://github.com/novnc/websockify.git /app/novnc/utils/websockify && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
 
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    xfce4 xfce4-terminal \
&& apt-get clean && rm -rf /var/lib/apt/lists/*
 
# Environment variable for Chrome
ENV CHROME_BIN="/usr/bin/google-chrome"
# Verify Chrome install
RUN google-chrome --version
# Copy startup script
COPY startup.sh /app/startup.sh
RUN chmod +x /app/startup.sh
# Expose VNC + noVNC ports
EXPOSE 5900 6080
# Default entrypoint
CMD ["/app/startup.sh"]
