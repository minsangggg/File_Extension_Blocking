const fixedList = document.getElementById("fixed-list");
const customInput = document.getElementById("custom-input");
const customAdd = document.getElementById("custom-add");
const customTags = document.getElementById("custom-tags");
const customCount = document.getElementById("custom-count");
const message = document.getElementById("message");
const fileInput = document.getElementById("file-input");
const fileUpload = document.getElementById("file-upload");
const uploadMessage = document.getElementById("upload-message");
const uploadText = document.getElementById("upload-text");
const uploadClose = document.getElementById("upload-close");
const fileName = document.getElementById("file-name");
const LENGTH_HINT = "\uCD5C\uB300 20\uC790\uAE4C\uC9C0 \uC785\uB825 \uAC00\uB2A5\uD569\uB2C8\uB2E4.";

let customItems = [];

function setMessage(text) {
  message.textContent = text || "";
}

function setUploadMessage(text, isError) {
  uploadText.textContent = text || "";
  const hasText = Boolean(text);
  uploadMessage.classList.toggle("success", hasText && !isError);
  uploadMessage.classList.toggle("toast", hasText);
  uploadMessage.classList.toggle("show", hasText);
}

fileInput.addEventListener("change", () => {
  if (!fileInput.files || fileInput.files.length === 0) {
    fileName.textContent = "파일을 선택하세요";
    return;
  }
  fileName.textContent = fileInput.files[0].name;
});

uploadClose.addEventListener("click", () => {
  setUploadMessage("", true);
});

customInput.addEventListener("input", () => {
  if (customInput.value.length >= 20) {
    if (!message.textContent || message.textContent === LENGTH_HINT) {
      setMessage(LENGTH_HINT);
    }
  } else if (message.textContent === LENGTH_HINT) {
    setMessage("");
  }
});

function updateCount() {
  customCount.textContent = `${customItems.length}/200`;
  customAdd.disabled = customItems.length >= 200;
}

function renderFixed(items) {
  fixedList.innerHTML = "";
  items.forEach(item => {
    const label = document.createElement("label");
    label.className = "fixed-item";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.checked = item.blocked;
    checkbox.addEventListener("change", async () => {
      try {
        const res = await fetch(`/api/extensions/fixed/${item.ext}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ blocked: checkbox.checked })
        });
        if (!res.ok) {
          throw new Error("update failed");
        }
      } catch (err) {
        checkbox.checked = !checkbox.checked;
        setMessage("고정 확장자 업데이트에 실패했습니다.");
      }
    });

    const span = document.createElement("span");
    span.textContent = item.ext;

    label.appendChild(checkbox);
    label.appendChild(span);
    fixedList.appendChild(label);
  });
}

function renderCustom() {
  customTags.innerHTML = "";
  customItems.forEach(item => {
    const tag = document.createElement("div");
    tag.className = "tag";
    const text = document.createElement("span");
    text.textContent = item.ext;
    const btn = document.createElement("button");
    btn.type = "button";
    btn.textContent = "X";
    btn.addEventListener("click", async () => {
      setMessage("");
      const res = await fetch(`/api/extensions/custom/${item.id}`, { method: "DELETE" });
      if (!res.ok) {
        setMessage("삭제에 실패했습니다.");
        return;
      }
      customItems = customItems.filter(x => x.id !== item.id);
      renderCustom();
      updateCount();
    });
    tag.appendChild(text);
    tag.appendChild(btn);
    customTags.appendChild(tag);
  });
  updateCount();
}

async function loadData() {
  setMessage("");
  const [fixedRes, customRes] = await Promise.all([
    fetch("/api/extensions/fixed"),
    fetch("/api/extensions/custom")
  ]);

  const fixedData = await fixedRes.json();
  const customData = await customRes.json();
  customItems = customData || [];
  renderFixed(fixedData || []);
  renderCustom();
}

customAdd.addEventListener("click", async () => {
  setMessage("");
  const value = customInput.value;
  if (!value) {
    setMessage("확장자를 입력해주세요.");
    return;
  }
  const res = await fetch("/api/extensions/custom", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ext: value })
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    setMessage(data.message || "추가에 실패했습니다.");
    return;
  }

  const created = await res.json();
  customItems.push(created);
  customInput.value = "";
  renderCustom();
});

fileUpload.addEventListener("click", async () => {
  setUploadMessage("", true);
  const file = fileInput.files && fileInput.files[0];
  if (!file) {
    setUploadMessage("파일을 선택해주세요.", true);
    return;
  }
  const formData = new FormData();
  formData.append("file", file);
  const res = await fetch("/api/files/upload", {
    method: "POST",
    body: formData
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    setUploadMessage(data.message || "업로드에 실패했습니다.", true);
    return;
  }
  setUploadMessage(data.message || "업로드 성공", false);
  fileInput.value = "";
  fileName.textContent = "파일을 선택하세요";
});

loadData().catch(() => setMessage("데이터 로딩에 실패했습니다."));
